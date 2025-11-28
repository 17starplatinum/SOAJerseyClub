package ru.itmo.cs.dandadan.client;

import jakarta.ws.rs.core.Response;

public interface HeroServiceClient {
    Response manipulateHumanBeingToTeam(Long teamId) throws Exception;
}
