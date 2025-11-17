package ru.itmo.cs.dandadan.client;

import jakarta.ws.rs.core.Response;
import ru.itmo.cs.dandadan.dto.response.TeamRequest;

public interface HeroServiceClient {
    Response manipulateHumanBeingToTeam(TeamRequest teamRequest);
}
