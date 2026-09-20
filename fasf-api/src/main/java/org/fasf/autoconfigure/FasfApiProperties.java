package org.fasf.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fasf.api")
public class FasfApiProperties {
    private boolean enable;
    private String basePackages;
    private ConnectionProvider connectionProvider;
    private LoopResources loopResources;
    private HttpClient httpClient;

    public static class HttpClient{
        private int connectTimeoutMillis;
        private long responseTimeout;
        private boolean keepAlive;

        public int getConnectTimeoutMillis() {
            return connectTimeoutMillis;
        }

        public void setConnectTimeoutMillis(int connectTimeoutMillis) {
            this.connectTimeoutMillis = connectTimeoutMillis;
        }

        public long getResponseTimeout() {
            return responseTimeout;
        }

        public void setResponseTimeout(long responseTimeout) {
            this.responseTimeout = responseTimeout;
        }

        public boolean isKeepAlive() {
            return keepAlive;
        }

        public void setKeepAlive(boolean keepAlive) {
            this.keepAlive = keepAlive;
        }
    }

    public static class LoopResources{
        private String prefix;
        private int minWorkerCount;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public int getMinWorkerCount() {
            return minWorkerCount;
        }

        public void setMinWorkerCount(int minWorkerCount) {
            this.minWorkerCount = minWorkerCount;
        }
    }

    public static class ConnectionProvider{
        private int maxConnections;
        private long maxIdleTime;
        private long maxLifeTime;
        private long evictInBackground;
        private long pendingAcquireTimeout;
        private int pendingAcquireMaxCount;

        public int getMaxConnections() {
            return maxConnections;
        }

        public void setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
        }

        public long getMaxIdleTime() {
            return maxIdleTime;
        }

        public void setMaxIdleTime(long maxIdleTime) {
            this.maxIdleTime = maxIdleTime;
        }

        public long getMaxLifeTime() {
            return maxLifeTime;
        }

        public void setMaxLifeTime(long maxLifeTime) {
            this.maxLifeTime = maxLifeTime;
        }

        public long getEvictInBackground() {
            return evictInBackground;
        }

        public void setEvictInBackground(long evictInBackground) {
            this.evictInBackground = evictInBackground;
        }

        public long getPendingAcquireTimeout() {
            return pendingAcquireTimeout;
        }

        public void setPendingAcquireTimeout(long pendingAcquireTimeout) {
            this.pendingAcquireTimeout = pendingAcquireTimeout;
        }

        public int getPendingAcquireMaxCount() {
            return pendingAcquireMaxCount;
        }

        public void setPendingAcquireMaxCount(int pendingAcquireMaxCount) {
            this.pendingAcquireMaxCount = pendingAcquireMaxCount;
        }
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(String basePackages) {
        this.basePackages = basePackages;
    }

    public ConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public LoopResources getLoopResources() {
        return loopResources;
    }

    public void setLoopResources(LoopResources loopResources) {
        this.loopResources = loopResources;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
