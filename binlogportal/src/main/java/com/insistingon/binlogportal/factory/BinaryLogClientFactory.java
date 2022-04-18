package com.insistingon.binlogportal.factory;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.insistingon.binlogportal.BinlogPortalException;
import com.insistingon.binlogportal.config.SyncConfig;
import com.insistingon.binlogportal.event.MultiEventHandlerListener;
import com.insistingon.binlogportal.event.lifecycle.ILifeCycleFactory;
import com.insistingon.binlogportal.event.parser.EventParserFactory;
import com.insistingon.binlogportal.position.BinlogPositionEntity;
import com.insistingon.binlogportal.position.IPositionHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BinaryLogClient Factory
 */
public class BinaryLogClientFactory implements IClientFactory {

    private ConcurrentHashMap<String, BinaryLogClient> cache = new ConcurrentHashMap<>();

    private IPositionHandler positionHandler;

    //LifeCycleEvent监听器
    private ILifeCycleFactory lifeCycleFactory;

    public IPositionHandler getPositionHandler() {
        return positionHandler;
    }

    public void setPositionHandler(IPositionHandler positionHandler) {
        this.positionHandler = positionHandler;
    }

    public ConcurrentHashMap<String, BinaryLogClient> getCache() {
        return cache;
    }

    public void setCache(ConcurrentHashMap<String, BinaryLogClient> cache) {
        this.cache = cache;
    }

    public ILifeCycleFactory getLifeCycleFactory() {
        return lifeCycleFactory;
    }

    public void setLifeCycleFactory(ILifeCycleFactory lifeCycleFactory) {
        this.lifeCycleFactory = lifeCycleFactory;
    }

    /**
     * 获取客户端
     *
     * @param syncConfig SyncConfig
     * @return BinaryLogClient
     */
    @Override
    public BinaryLogClient getClient(SyncConfig syncConfig) throws BinlogPortalException {
        String key = syncConfig.toString();
        //有缓存拿缓存里的
        if (cache.get(key) != null) {
            return cache.get(key);
        } else {
            //创建客户端
            BinaryLogClient client = new BinaryLogClient(
                    syncConfig.getHost(),
                    syncConfig.getPort(),
                    syncConfig.getUserName(),
                    syncConfig.getPassword()
            );
            EventDeserializer eventDeserializer = new EventDeserializer();
            eventDeserializer.setCompatibilityMode(
                    EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
                    EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
            );
            client.setEventDeserializer(eventDeserializer);
            //设置slave的serverId，不同集群中，两个机器不能相同
            client.setServerId(getRandomServerId());

            //处理binlog位点信息
            if (positionHandler != null && positionHandler.getPosition(syncConfig) != null) {
                BinlogPositionEntity positionEntity = positionHandler.getPosition(syncConfig);
                if (positionEntity != null
                        && !StringUtils.isBlank(positionEntity.getBinlogName())
                        && positionEntity.getPosition() != null) {
                    client.setBinlogFilename(positionEntity.getBinlogName());
                    long position = positionEntity.getPosition() != null ? positionEntity.getPosition() : 0L;
                    client.setBinlogPosition((position));
                }
            }

            //创建多事件统一处理器
            MultiEventHandlerListener multiEventHandlerListener = new MultiEventHandlerListener();
            //设置事件解析器
            multiEventHandlerListener.setEventParserDispatcher(EventParserFactory.getEventParserDispatcher(syncConfig));
            //保存配置信息
            multiEventHandlerListener.setSyncConfig(syncConfig);
            //设置binlog位点信息
            multiEventHandlerListener.setPositionHandler(positionHandler);
            //注册配置信息中的事件处理器
            syncConfig.getEventHandlerList().forEach(multiEventHandlerListener::registerEventHandler);

            //注册client的监听器
            client.registerEventListener(multiEventHandlerListener);
            client.registerLifecycleListener(lifeCycleFactory.getLifeCycleListener(syncConfig));

            cache.put(key, client);
            return client;
        }
    }

    @Override
    public BinaryLogClient getCachedClient(SyncConfig syncConfig) {
        String key = syncConfig.toString();
        return cache.get(key);
    }


    private long getRandomServerId() {
        try {
            return new SecureRandom().nextLong();
        } catch (Throwable e) {
            return RandomUtils.nextLong();
        }
    }
}
