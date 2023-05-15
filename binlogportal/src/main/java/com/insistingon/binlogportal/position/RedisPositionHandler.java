package com.insistingon.binlogportal.position;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insistingon.binlogportal.BinlogPortalException;
import com.insistingon.binlogportal.config.RedisConfig;
import com.insistingon.binlogportal.config.SyncConfig;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPositionHandler implements IPositionHandler {

    private RedisConfig redisConfig;
    private Jedis jedis;
    private JedisPool jedisPool;

    public RedisPositionHandler(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        if (!StringUtils.isBlank(redisConfig.getAuth())) {
            jedisPool = new JedisPool(jedisPoolConfig, redisConfig.getHost(), redisConfig.getPort(), 1000, redisConfig.getAuth());
        } else {
            jedisPool = new JedisPool(jedisPoolConfig, redisConfig.getHost(), redisConfig.getPort(), 1000);
        }
    }

    @Override
    public BinlogPositionEntity getPosition(SyncConfig syncConfig) throws BinlogPortalException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String position = jedis.get(syncConfig.getHost() + ":" + syncConfig.getPort());
            if (position != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(position, BinlogPositionEntity.class);
            }
        } catch (JsonProcessingException e) {
            return null;
        } finally {
            if (jedis != null) jedis.close();
        }
        return null;
    }

    @Override
    public void savePosition(SyncConfig syncConfig, BinlogPositionEntity binlogPositionEntity) throws BinlogPortalException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            ObjectMapper objectMapper = new ObjectMapper();
            jedis.set(syncConfig.getHost() + ":" + syncConfig.getPort(), objectMapper.writeValueAsString(binlogPositionEntity));
        } catch (JsonProcessingException e) {
            throw new BinlogPortalException("save position error!" + binlogPositionEntity.toString(), e);
        } finally {
            if (jedis != null) jedis.close();
        }
    }
}
