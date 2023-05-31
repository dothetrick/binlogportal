package com.insistingon.binlogportal.position;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insistingon.binlogportal.BinlogPortalException;
import com.insistingon.binlogportal.config.RedisConfig;
import com.insistingon.binlogportal.config.SyncConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import java.util.*;
import java.util.stream.Collectors;

public class RedisPositionHandler implements IPositionHandler {

    private static final Logger log = LoggerFactory.getLogger(RedisPositionHandler.class);

    private RedisConfig redisConfig;
    private Jedis jedis;
    private JedisPool jedisPool;

    private JedisCluster jedisCluster;

    public RedisPositionHandler(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        // 兼容redis cluster
        if (redisConfig.getCluster() != null) {
            if (redisConfig.getCluster().getNodes() != null && !redisConfig.getCluster().getNodes().isEmpty()) {
                log.info("redis cluster mode");
                Set<HostAndPort> nodes = redisConfig.getCluster().getNodes().stream().map(item -> {
                    String[] args = item.split(":");
                    if (args.length == 2) {
                        return new HostAndPort(args[0], Integer.parseInt(args[1]));
                    } else {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toSet());

                if (!StringUtils.isBlank(redisConfig.getAuth())) {
                    jedisCluster = new JedisCluster(nodes, jedisPoolConfig);
                } else {
                    // 有密码
                    jedisCluster = new JedisCluster(nodes, 1000, 1000, 10, redisConfig.getAuth(), jedisPoolConfig);
                }
            }

        } else {
            log.info("redis standalone mode");
            if (!StringUtils.isBlank(redisConfig.getAuth())) {
                jedisPool = new JedisPool(jedisPoolConfig, redisConfig.getHost(), redisConfig.getPort(), 1000,
                        redisConfig.getAuth());
            } else {
                jedisPool = new JedisPool(jedisPoolConfig, redisConfig.getHost(), redisConfig.getPort(), 1000);
            }
            // 测试
            jedis = jedisPool.getResource();
            log.info("redis standalone ping:{}", jedis.ping());
        }
    }

    @Override
    public BinlogPositionEntity getPosition(SyncConfig syncConfig) throws BinlogPortalException {
        Jedis jedis = null;
        try {
            // 兼容redis cluster
            String key = syncConfig.getHost() + ":" + syncConfig.getPort();
            String value = null;
            if (jedisCluster != null) {
                value = jedisCluster.get(key);
            } else {
                jedis = jedisPool.getResource();
                value = jedis.get(key);
            }
            if (value != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(value, BinlogPositionEntity.class);
            }
        } catch (JsonProcessingException e) {
            return null;
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return null;
    }

    @Override
    public void savePosition(SyncConfig syncConfig, BinlogPositionEntity binlogPositionEntity)
            throws BinlogPortalException {
        Jedis jedis = null;
        String key = syncConfig.getHost() + ":" + syncConfig.getPort();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 兼容redis cluster
            if (jedisCluster != null) {
                jedisCluster.set(key, objectMapper.writeValueAsString(binlogPositionEntity));
            } else {
                jedis = jedisPool.getResource();
                jedis.set(key, objectMapper.writeValueAsString(binlogPositionEntity));
            }
        } catch (JsonProcessingException e) {
            throw new BinlogPortalException("save position error!" + binlogPositionEntity.toString(), e);
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }
}
