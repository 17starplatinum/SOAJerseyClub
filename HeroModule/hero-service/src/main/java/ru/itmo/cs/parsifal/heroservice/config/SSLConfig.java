package ru.itmo.cs.parsifal.heroservice.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;

@Configuration
public class SSLConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(
            @Value("${server.ssl.key-store}") Resource keyStore,
            @Value("${server.ssl.key-store-password}") String keyPass,
            @Value("${server.ssl.trust-store}") Resource trustStore,
            @Value("${server.ssl.trust-store-password}") String trustPass
    ) throws Exception {

        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(keyStore.getInputStream(), keyPass.toCharArray());

        KeyStore ts = KeyStore.getInstance("PKCS12");
        ts.load(trustStore.getInputStream(), trustPass.toCharArray());

        SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(ks, keyPass.toCharArray())
                .loadTrustMaterial(ts, null)
                .build();

        CloseableHttpClient client = HttpClients.custom()
                .setSSLContext(sslContext)
                .build();

        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
    }
}