package ru.itmo.cs.dandadan.config;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import ru.itmo.cs.dandadan.config.qualifier.ConnectTimeoutSec;
import ru.itmo.cs.dandadan.config.qualifier.HttpClientMaxPerRoute;
import ru.itmo.cs.dandadan.config.qualifier.HttpClientMaxTotal;
import ru.itmo.cs.dandadan.config.qualifier.ResponseTimeoutSec;

@Dependent
public class HttpClientConfig {

    private final int connectTimeoutSec;
    private final int responseTimeoutSec;
    private final int maxTotal;
    private final int maxPerRoute;

    @Inject
    public HttpClientConfig(
            @ConnectTimeoutSec Integer connectTimeoutSec,
            @ResponseTimeoutSec Integer responseTimeoutSec,
            @HttpClientMaxTotal Integer maxTotal,
            @HttpClientMaxPerRoute Integer maxPerRoute
    ) {
        this.connectTimeoutSec = connectTimeoutSec;
        this.responseTimeoutSec = responseTimeoutSec;
        this.maxTotal = maxTotal;
        this.maxPerRoute = maxPerRoute;
    }

    public RequestConfig createRequestConfig() {
        return RequestConfig.custom()
                .setResponseTimeout(Timeout.ofSeconds(responseTimeoutSec))
                .build();
    }

    public PoolingHttpClientConnectionManager createConnectionManager() {
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(connectTimeoutSec))
                .build();

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setDefaultConnectionConfig(connectionConfig);
        connManager.setMaxTotal(maxTotal);
        connManager.setDefaultMaxPerRoute(maxPerRoute);
        return connManager;
    }
}
