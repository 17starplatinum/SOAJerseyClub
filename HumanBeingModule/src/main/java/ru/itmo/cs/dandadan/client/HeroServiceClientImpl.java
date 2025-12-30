package ru.itmo.cs.dandadan.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import ru.itmo.cs.dandadan.config.qualifier.BaseUrl;
import ru.itmo.cs.dandadan.dto.response.TeamResponse;

import java.io.IOException;

@ApplicationScoped
public class HeroServiceClientImpl implements HeroServiceClient {
    private final String baseUrl;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper mapper;
    private final HttpClientResponseHandler<TeamResponse> responseHandler;

    @Inject
    public HeroServiceClientImpl(
            @BaseUrl String baseUrl,
            CloseableHttpClient httpClient,
            ObjectMapper mapper
    ) {
        this.baseUrl = baseUrl;
        this.httpClient = httpClient;
        this.mapper = mapper;
        this.responseHandler = createResponseHandler();
    }

    private HttpClientResponseHandler<TeamResponse> createResponseHandler() {
        return response -> {
            if (response.getCode() != HttpStatus.SC_OK) {
                throw new IOException("Request to People API failed: HTTP "
                        + response.getCode() + " " + response.getReasonPhrase());
            }
            return mapper.readValue(response.getEntity().getContent(), TeamResponse.class);
        };
    }

    @Override
    public Long manipulateHumanBeingToTeam(Long teamId) {
        String url = baseUrl + teamId;
        HttpGet get = new HttpGet(url);
        TeamResponse response;
        try {
            response = httpClient.execute(get, responseHandler);
        } catch (IOException e) {
            throw new ConflictException(teamId);
        }
        return response.getId();
    }
}
