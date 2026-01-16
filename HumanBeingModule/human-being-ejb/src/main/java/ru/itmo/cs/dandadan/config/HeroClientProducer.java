package ru.itmo.cs.dandadan.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import ru.itmo.cs.dandadan.client.HeroServiceClient;
import ru.itmo.cs.dandadan.client.HeroServiceClientImpl;
import ru.itmo.cs.dandadan.config.qualifier.BaseUrl;

@ApplicationScoped
public class HeroClientProducer {
    @Produces
    @ApplicationScoped
    public HeroServiceClient getHeroServiceClient(
            @BaseUrl String baseUrl,
            CloseableHttpClient httpClient,
            ObjectMapper mapper
    ) {
        return new HeroServiceClientImpl(baseUrl, httpClient, mapper);
    }
}
