package com.insistingon.binlogportal.config;

import java.util.List;
import java.util.StringJoiner;

/**
 * 内部抽象redis配置
 */
public class RedisConfig {
    String host;
    Integer port;
    String auth;

    private Cluster cluster;


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

    public RedisConfig(String host, Integer port, String auth, Cluster cluster) {
        this.host = host;
        this.port = port;
        this.auth = auth;
        this.cluster = cluster;
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

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RedisConfig.class.getSimpleName() + "[", "]")
                .add("host='" + host + "'")
                .add("port=" + port)
                .add("auth='" + auth + "'")
                .add("cluster=" + cluster)
                .toString();
    }

    public static class Cluster {

        /**
         * Comma-separated list of "host:port" pairs to bootstrap from. This represents an
         * "initial" list of cluster nodes and is required to have at least one entry.
         */
        private List<String> nodes;

        /**
         * Maximum number of redirects to follow when executing commands across the
         * cluster.
         */
        private Integer maxRedirects;

        public List<String> getNodes() {
            return this.nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }

        public Integer getMaxRedirects() {
            return this.maxRedirects;
        }

        public void setMaxRedirects(Integer maxRedirects) {
            this.maxRedirects = maxRedirects;
        }

    }
}
