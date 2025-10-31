package ru.itmo.cs.dandadan.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.itmo.cs.dandadan.dto.response.TeamRequest;

@Path("{hero.service.url:https://localhost:15478/api/v1/heroes/teams}")
@ApplicationScoped
public interface HeroServiceClient {
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response manipulateHumanBeingToTeam(TeamRequest teamRequest);
}
