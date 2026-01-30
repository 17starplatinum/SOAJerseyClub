package ru.itmo.cs.dandadan.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

@ApplicationScoped
public class HttpClientProducer {

    @Inject
    private HttpClientConfig httpClientConfig;

    @Produces
    @ApplicationScoped
    public CloseableHttpClient produceHttpClient() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream ksStream = getClass().getClassLoader()
                    .getResourceAsStream("keystore/human-being-service-keystore.p12")) {
                if (ksStream == null) {
                    throw new RuntimeException("Keystore not found in classpath");
                }
                keyStore.load(ksStream, "changeit".toCharArray());
            }

            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            try (InputStream tsStream = getClass().getClassLoader()
                    .getResourceAsStream("keystore/human-being-service-truststore.p12")) {
                if (tsStream == null) {
                    throw new RuntimeException("Truststore not found in classpath");
                }
                trustStore.load(tsStream, "changeit".toCharArray());
            }

            SSLContext sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(keyStore, "changeit".toCharArray())
                    .loadTrustMaterial(trustStore, null)
                    .build();
            SSLConnectionSocketFactory sslConnectionSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(sslContext)
                    .setTlsVersions(TLS.V_1_2, TLS.V_1_3)
                    .setHostnameVerifier(new DefaultHostnameVerifier())
                    .build();
            HttpClientConnectionManager connectionManager =
                    PoolingHttpClientConnectionManagerBuilder.create()
                            .setSSLSocketFactory(sslConnectionSocketFactory)
                            .setMaxConnTotal(httpClientConfig.createConnectionManager().getMaxTotal())
                            .setMaxConnPerRoute(httpClientConfig.createConnectionManager().getDefaultMaxPerRoute())
                            .build();

            return HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(httpClientConfig.createRequestConfig())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create SSL-enabled HTTP client", e);
        }
    }

    public void disposeHttpClient(@Disposes CloseableHttpClient client) throws IOException {
        if (client != null) {
            client.close();
        }
    }
}
