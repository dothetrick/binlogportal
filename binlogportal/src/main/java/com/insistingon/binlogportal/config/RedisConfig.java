package com.insistingon.binlogportal.config;

/**
 * 内部抽象redis配置
 */
public class RedisConfig {
    String host;
    Integer port;
    String auth;

    public RedisConfig() {
    }

    public RedisConfig(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public RedisConfig(String host, Integer port, String auth) {
        this.host = host;
        this.port = port;
        this.auth = auth;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    @Override
    public String toString() {
        return "RedisConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", auth='" + auth + '\'' +
                '}';
    }
}
