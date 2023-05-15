package com.insistingon.binlogportal.distributed;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.insistingon.binlogportal.BinlogPortalException;
import com.insistingon.binlogportal.config.BinlogPortalConfig;
import com.insistingon.binlogportal.config.RedisConfig;
import com.insistingon.binlogportal.config.SyncConfig;
import com.insistingon.binlogportal.factory.IClientFactory;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RedisDistributedHandler implements IDistributedHandler {

    private final Logger log = LoggerFactory.getLogger(RedisDistributedHandler.class);

    //redis配置，支持集群模式
    RedisConfig redisConfig;

    public RedisDistributedHandler(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    @Override
    public void start(BinlogPortalConfig binlogPortalConfig) throws BinlogPortalException {
        if (redisConfig == null) {
            throw new BinlogPortalException("redis config can not be null");
        }
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress("redis://" + redisConfig.getHost() + ":" + redisConfig.getPort());
        if (!StringUtils.isBlank(redisConfig.getAuth())) {
            singleServerConfig.setPassword(redisConfig.getAuth());
        }
        config.setLockWatchdogTimeout(10000L);
        RedissonClient redisson = Redisson.create(config);

        //新建工厂对象
        IClientFactory binaryLogClientFactory = binlogPortalConfig.getClientFactory();
        binaryLogClientFactory.setPositionHandler(binlogPortalConfig.getPositionHandler());
        binaryLogClientFactory.setLifeCycleFactory(binlogPortalConfig.getLifeCycleFactory());

        Map<SyncConfig, BinaryLogClient> clientMapCache = new HashMap<>();

        //定时创建客户端,抢到锁的就创建
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                binlogPortalConfig.getSyncConfigMap().forEach((key, syncConfig) -> {
                    String lockStr = Md5Crypt.md5Crypt(syncConfig.toString().getBytes(), null, "");
                    RLock lock = redisson.getLock(lockStr);
                    try {
                        BinaryLogClient client = clientMapCache.get(syncConfig);
                        //已经成功连接的不再抢锁
                        if (client == null || !clientMapCache.get(syncConfig).isConnected()) {
                            if (lock.tryLock()) {
                                if (client == null) {
                                    client = binaryLogClientFactory.getClient(syncConfig);
                                    clientMapCache.put(syncConfig, client);
                                }
                                //重新连接前，设置当前的位点
                                binaryLogClientFactory.setConnectPosition(syncConfig, client);
                                client.connect();
                            }
                        }
                    } catch (BinlogPortalException | IOException e) {
                        log.error(e.getMessage(), e);
                        lock.unlock();
                    }
                });
            }
        }, 0, 10 * 1000);
    }

    public Boolean canBuild(SyncConfig syncConfig) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        config.setLockWatchdogTimeout(10000L);
        RedissonClient redisson = Redisson.create(config);
        RLock lock = redisson.getLock("myLock");
        lock.lock();

        return null;
    }

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        config.setLockWatchdogTimeout(10000L);
        RedissonClient redisson = Redisson.create(config);
        RLock lock = redisson.getLock("myLock");
        lock.lock();
    }
}
