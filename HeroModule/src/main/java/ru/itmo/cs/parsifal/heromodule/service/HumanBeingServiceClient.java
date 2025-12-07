package ru.itmo.cs.parsifal.heromodule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.itmo.cs.parsifal.heromodule.model.HumanBeingFullResponse;
import ru.itmo.cs.parsifal.heromodule.model.HumanBeingPaginatedResponse;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HumanBeingServiceClient {

    private final RestTemplate restTemplate;

    @Value("${humanbeing.service.url:https://localhost:15478/api/v1}")
    private String humanBeingServiceUrl;

    public List<HumanBeingFullResponse> getHumanBeings(Boolean realHeroOnly) {
        try {
            String url = humanBeingServiceUrl + "/human-beings";
            if (realHeroOnly != null) {
                url += "?filter=realHero[eq]=" + realHeroOnly;
            }

            ResponseEntity<HumanBeingPaginatedResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return response.getBody() != null
                    ? response.getBody().getHumanBeingGetResponseDtos()
                    : Collections.emptyList();

        } catch (org.springframework.web.client.ResourceAccessException e) {
            log.error("Timeout calling HumanBeing service", e);
            throw new RuntimeException("Upstream timeout: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error calling HumanBeing service", e);
            throw new RuntimeException("Upstream service unavailable", e);
        }
    }

    public HumanBeingFullResponse updateHumanBeing(Long humanId, HumanBeingFullResponse humanBeing) {
        try {
            String url = humanBeingServiceUrl + "/human-beings/" + humanId;

            ResponseEntity<HumanBeingFullResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(humanBeing),
                    HumanBeingFullResponse.class
            );
            return response.getBody();

        } catch (Exception e) {
            log.error("Error updating human being", e);
            throw new RuntimeException("Upstream service unavailable", e);
        }
    }

    public List<HumanBeingFullResponse> getHumanBeingsByTeamId(Long teamId) {
        try {
            String url = humanBeingServiceUrl + "/human-beings?filter=teamId[eq]=" + teamId;
            ResponseEntity<HumanBeingPaginatedResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return response.getBody() != null
                    ? response.getBody().getHumanBeingGetResponseDtos()
                    : Collections.emptyList();

        } catch (org.springframework.web.client.ResourceAccessException e) {
            log.error("Service unavailable getting human beings by team id", e);
            throw new RuntimeException("Upstream service unavailable: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error getting human beings by team id", e);
            throw new RuntimeException("Upstream service error", e);
        }
    }
}