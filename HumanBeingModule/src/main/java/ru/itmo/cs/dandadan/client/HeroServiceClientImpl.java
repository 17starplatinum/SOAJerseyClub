package ru.itmo.cs.dandadan.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;
import ru.itmo.cs.dandadan.config.qualifier.BaseUrl;
import ru.itmo.cs.dandadan.dto.response.TeamRequest;
import ru.itmo.cs.dandadan.dto.response.TeamResponse;
import ru.itmo.cs.dandadan.exception.ConflictException;

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
                throw new IOException("Request to People API failed: HTTP " + response.getCode());
            }
            return mapper.readValue(response.getEntity().getContent(), TeamResponse.class);
        };
    }

    @Override
    public Response manipulateHumanBeingToTeam(TeamRequest teamRequest) {
        HttpPatch patch = new HttpPatch(baseUrl);
        TeamResponse response;
        try {
            String json = mapper.writeValueAsString(teamRequest);
            patch.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
            patch.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            response = httpClient.execute(patch, responseHandler);
        } catch (IOException e) {
            throw new ConflictException(teamRequest.getOperation(), teamRequest.getTeamId());
        }
        String result = String.valueOf(response.getId());
        return Response.ok(result).build();
    }
}
