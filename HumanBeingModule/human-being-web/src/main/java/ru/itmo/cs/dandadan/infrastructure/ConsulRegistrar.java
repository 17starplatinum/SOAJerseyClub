package ru.itmo.cs.dandadan.infrastructure;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

@Singleton
@Startup
public class ConsulRegistrar {

    private static final Logger log = Logger.getLogger(ConsulRegistrar.class.getName());

    private final String serviceName = "human-being-service";
    private final String consulHost;
    private final int consulPort;

    private final String serviceHost;
    private final int servicePort;
    private final String serviceId;
    private final String healthCheckUrl;

    private CloseableHttpClient httpClient;

    public ConsulRegistrar() {
        this.consulHost = System.getenv().getOrDefault("CONSUL_HOST", "localhost");
        this.consulPort = Integer.parseInt(System.getenv().getOrDefault("CONSUL_PORT", "8500"));
        this.serviceHost = System.getenv().getOrDefault("SERVICE_HOST", "host.docker.internal");
        this.servicePort = 8080;
        this.serviceId = serviceName + "-" + UUID.randomUUID().toString().substring(0, 8);
        this.healthCheckUrl = "http://host.docker.internal:8080/api/v1/health-check";
    }

    @PostConstruct
    public void register() {
        httpClient = HttpClients.createDefault();
        String registrationUrl = "http://" + consulHost + ":" + consulPort + "/v1/agent/service/register";

        String payload = String.format("{\n" +
        "\"Name\": \"%s\",\n" +
        "\"ID\": \"%s\",\n" +
        "\"Address\": \"%s\",\n" +
        "\"Port\": %d,\n" +
        "\"Check\": {\n" +
          "\"HTTP\": \"%s\",\n" +
          "\"Interval\": \"10s\",\n" +
          "\"Timeout\": \"3s\",\n" +
          "\"tls_skip_verify\": true\n" +
        "}\n" +
      "}", serviceName, serviceId, serviceHost, servicePort, healthCheckUrl);

        try {
            var request = ClassicRequestBuilder.put(registrationUrl)
                    .setEntity(new StringEntity(payload, ContentType.APPLICATION_JSON))
                    .build();
            httpClient.execute(request, response -> {
                int status = response.getCode();
                if (status >= 200 && status < 300) {
                    log.info("Successfully registered in Consul");
                } else {
                    log.severe("Failed to register in Consul: HTTP " + status);
                }
                return null;
            });
        } catch (IOException e) {
            log.severe("Exception during Consul registration: " + e.getMessage());
        }
    }

    @PreDestroy
    public void close() {
        String deregisterUrl = "http://" + consulHost + ":" + consulPort + "/v1/agent/service/deregister/" + serviceId;
        try {
            var request = ClassicRequestBuilder.put(deregisterUrl).build();
            httpClient.execute(request, response -> {
                log.info("Deregistered from Consul");
                return null;
            });
        } catch (IOException e) {
            log.warning("Failed to deregister from Consul: " + e.getMessage());
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                log.warning("Failed to close HTTP client: " + e.getMessage());
            }
        }
    }
}
