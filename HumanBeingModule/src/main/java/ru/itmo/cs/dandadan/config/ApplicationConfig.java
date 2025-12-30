package ru.itmo.cs.dandadan.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import ru.itmo.cs.dandadan.config.qualifier.*;

@ApplicationScoped
public class ApplicationConfig {
    private final String peopleServiceBaseUrl = System.getenv().getOrDefault("PEOPLE_SERVICE_URL", "https://localhost:15479/api/v1/heroes/teams/");
    private final Integer httpClientConnectTimeoutSec = getIntEnv("HTTP_CLIENT_CONNECT_TIMEOUT", 5);
    private final Integer httpClientResponseTimeoutSec = getIntEnv("HTTP_CLIENT_RESPONSE_TIMEOUT", 10);
    private final Integer httpClientMaxTotal = getIntEnv("HTTP_CLIENT_MAX_TOTAL", 10);
    private final Integer httpClientMaxPerRoute = getIntEnv("HTTP_CLIENT_MAX_PER_ROUTE", 5);

    private static int getIntEnv(String name, int defaultValue) {
        String val = System.getenv(name);
        if (val != null) {
            try {
                return Integer.parseInt(val);
            } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    @Produces
    @BaseUrl
    public String producePeopleServiceBaseUrl() {
        return peopleServiceBaseUrl;
    }

    @Produces
    @ConnectTimeoutSec
    public Integer produceHttpClientConnectTimeoutSec() {
        return httpClientConnectTimeoutSec;
    }

    @Produces
    @ResponseTimeoutSec
    public Integer produceHttpClientResponseTimeoutSec() {
        return httpClientResponseTimeoutSec;
    }

    @Produces
    @HttpClientMaxTotal
    public Integer produceHttpClientMaxTotal() {
        return httpClientMaxTotal;
    }

    @Produces
    @HttpClientMaxPerRoute
    public Integer produceHttpClientMaxPerRoute() {
        return httpClientMaxPerRoute;
    }
}
