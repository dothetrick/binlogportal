package com.insistingon.binlogportal;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.insistingon.binlogportal.config.BinlogPortalConfig;
import com.insistingon.binlogportal.config.RedisConfig;
import com.insistingon.binlogportal.config.SyncConfig;
import com.insistingon.binlogportal.distributed.RedisDistributedHandler;
import com.insistingon.binlogportal.factory.IClientFactory;
import com.insistingon.binlogportal.position.RedisPositionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Starter
 */
public class BinlogPortalStarter {
    private final Logger log = LoggerFactory.getLogger(BinlogPortalStarter.class);

    private BinlogPortalConfig binlogPortalConfig;

    public BinlogPortalConfig getBinlogPortalConfig() {
        return binlogPortalConfig;
    }

    public void setBinlogPortalConfig(BinlogPortalConfig binlogPortalConfig) {
        this.binlogPortalConfig = binlogPortalConfig;
    }

    /**
     * main start method
     */
    public void start() throws BinlogPortalException {
        if (binlogPortalConfig.getDistributedHandler() != null) {
            binlogPortalConfig.getDistributedHandler().start(binlogPortalConfig);
        } else {
            SingleStart();
        }
    }

    private void SingleStart() {
        //新建工厂对象
        IClientFactory binaryLogClientFactory = binlogPortalConfig.getClientFactory();
        binaryLogClientFactory.setPositionHandler(binlogPortalConfig.getPositionHandler());
        binaryLogClientFactory.setLifeCycleFactory(binlogPortalConfig.getLifeCycleFactory());

        //生成全部client
        List<BinaryLogClient> binaryLogClientList = new ArrayList<>();
        binlogPortalConfig.getSyncConfigMap().forEach((key, syncConfig) -> {
            try {
                binaryLogClientList.add(binaryLogClientFactory.getClient(syncConfig));
            } catch (BinlogPortalException e) {
                log.error(e.getMessage(), e);
            }
        });

        //执行
        binaryLogClientList.forEach(binaryLogClient -> {
            new Thread(() -> {
                try {
                    binaryLogClient.setHeartbeatInterval(10 * 1000L);
                    binaryLogClient.connect();
                } catch (IOException e) {
                    log.error("binaryLogClient connect error!" + binaryLogClient.toString());
                }
            }).start();
        });
    }

    public BinaryLogClient getClientByDbKey(String key) {
        SyncConfig syncConfig = binlogPortalConfig.getSyncConfigMap().get(key);
        if (syncConfig == null) {
            return null;
        }
        return binlogPortalConfig.getClientFactory().getCachedClient(syncConfig);
    }

    public static void main(String[] args) {
        SyncConfig syncConfig = new SyncConfig();
        syncConfig.setHost("0.0.0.0");
        syncConfig.setPort(3306);
        syncConfig.setUserName("binlogportal");
        syncConfig.setPassword("123456");
        syncConfig.setEventHandlerList(Collections.singletonList(eventEntity -> System.out.println(eventEntity.getJsonFormatData())));

        BinlogPortalConfig binlogPortalConfig = new BinlogPortalConfig();
        binlogPortalConfig.addSyncConfig("d1", syncConfig);

        RedisConfig redisConfig = new RedisConfig("127.0.0.1", 6379);
        RedisPositionHandler redisPositionHandler = new RedisPositionHandler(redisConfig);
        binlogPortalConfig.setPositionHandler(redisPositionHandler);

        binlogPortalConfig.setDistributedHandler(new RedisDistributedHandler(redisConfig));

        BinlogPortalStarter binlogPortalStarter = new BinlogPortalStarter();
        binlogPortalStarter.setBinlogPortalConfig(binlogPortalConfig);
        try {
            binlogPortalStarter.start();
        } catch (BinlogPortalException e) {
            e.printStackTrace();
        }
    }
}
