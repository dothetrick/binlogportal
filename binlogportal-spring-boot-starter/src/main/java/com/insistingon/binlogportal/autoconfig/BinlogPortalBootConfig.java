package com.insistingon.binlogportal.autoconfig;


import com.insistingon.binlogportal.config.RedisConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "binlogportal")
public class BinlogPortalBootConfig {
    private Map<String, DbConfig> dbConfig;
    private Boolean enable;
    private Boolean distributedEnable;
    private RedisConfig distributedRedis;
    private RedisConfig positionRedis;
    private HttpHandlerConfig httpHandler;

    public Map<String, DbConfig> getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(Map<String, DbConfig> dbConfig) {
        this.dbConfig = dbConfig;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getDistributedEnable() {
        return distributedEnable;
    }

    public void setDistributedEnable(Boolean distributedEnable) {
        this.distributedEnable = distributedEnable;
    }

    public RedisConfig getDistributedRedis() {
        return distributedRedis;
    }

    public void setDistributedRedis(RedisConfig distributedRedis) {
        this.distributedRedis = distributedRedis;
    }

    public RedisConfig getPositionRedis() {
        return positionRedis;
    }

    public void setPositionRedis(RedisConfig positionRedis) {
        this.positionRedis = positionRedis;
    }

    public HttpHandlerConfig getHttpHandler() {
        return httpHandler;
    }

    public void setHttpHandler(HttpHandlerConfig httpHandler) {
        this.httpHandler = httpHandler;
    }
}
