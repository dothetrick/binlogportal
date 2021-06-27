package com.insistingon.binlogportal.autoconfig;

import com.insistingon.binlogportal.BinlogPortalException;
import com.insistingon.binlogportal.BinlogPortalStarter;
import com.insistingon.binlogportal.config.BinlogPortalConfig;
import com.insistingon.binlogportal.config.RedisConfig;
import com.insistingon.binlogportal.config.SyncConfig;
import com.insistingon.binlogportal.distributed.RedisDistributedHandler;
import com.insistingon.binlogportal.event.handler.HttpRequestEventHandler;
import com.insistingon.binlogportal.event.handler.IEventHandler;
import com.insistingon.binlogportal.event.handler.IHttpCallback;
import com.insistingon.binlogportal.position.RedisPositionHandler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(BinlogPortalBootConfig.class)
@ConditionalOnClass(BinlogPortalStarter.class)
@ConditionalOnProperty(prefix = "binlogportal", value = "enable", havingValue = "true")
public class BinlogPortalAutoConfiguration {
    @Autowired
    BinlogPortalBootConfig binlogPortalBootConfig;

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean(BinlogPortalStarter.class)
    public BinlogPortalStarter binlogPortalStarter() throws BinlogPortalException {
        BinlogPortalConfig binlogPortalConfig = new BinlogPortalConfig();
        Map<String, IEventHandler> eventHandlerList = applicationContext.getBeansOfType(IEventHandler.class);

        //common http event handler
        HttpRequestEventHandler httpRequestEventHandler = null;
        HttpHandlerConfig httpHandlerConfig = binlogPortalBootConfig.getHttpHandler();
        if (httpHandlerConfig != null && httpHandlerConfig.getUrlList() != null) {
            httpRequestEventHandler = new HttpRequestEventHandler();
            httpRequestEventHandler.setUrlList(binlogPortalBootConfig.getHttpHandler().getUrlList());
            if (!StringUtils.isBlank(httpHandlerConfig.getResultCallback())) {
                httpRequestEventHandler.setHttpCallback(applicationContext.getBeansOfType(IHttpCallback.class).get(httpHandlerConfig.getResultCallback()));
            }
        }

        //dbconfig list
        for (Map.Entry<String, DbConfig> entry : binlogPortalBootConfig.getDbConfig().entrySet()) {
            String key = entry.getKey();
            DbConfig val = entry.getValue();
            SyncConfig syncConfig = new SyncConfig();
            syncConfig.setHost(val.getHost());
            syncConfig.setPort(val.getPort());
            syncConfig.setUserName(val.getUserName());
            syncConfig.setPassword(val.getPassword());
            if (val.getHandlerList() != null) {
                val.getHandlerList().forEach(eventHandler -> {
                    syncConfig.addEventHandlerList(eventHandlerList.get(eventHandler));
                });
            }
            if (httpRequestEventHandler != null) {
                syncConfig.addEventHandlerList(httpRequestEventHandler);
            }
            binlogPortalConfig.addSyncConfig(key, syncConfig);
        }

        //binlog position config
        RedisConfig positionRedisConfig = binlogPortalBootConfig.getPositionRedis();
        if (positionRedisConfig != null) {
            RedisPositionHandler redisPositionHandler = new RedisPositionHandler(positionRedisConfig);
            binlogPortalConfig.setPositionHandler(redisPositionHandler);
        } else {
            throw new BinlogPortalException("binlog position redis should not be null");
        }

        //distributed config
        if (binlogPortalBootConfig.getDistributedEnable()) {
            binlogPortalConfig.setDistributedHandler(new RedisDistributedHandler(binlogPortalBootConfig.getDistributedRedis()));
        }

        BinlogPortalStarter binlogPortalStarter = new BinlogPortalStarter();
        binlogPortalStarter.setBinlogPortalConfig(binlogPortalConfig);
        return binlogPortalStarter;
    }
}
