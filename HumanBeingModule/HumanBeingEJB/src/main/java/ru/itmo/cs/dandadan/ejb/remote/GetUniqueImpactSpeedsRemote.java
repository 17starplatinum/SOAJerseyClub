package ru.itmo.cs.dandadan.ejb.remote;

import jakarta.ejb.Remote;
import ru.itmo.cs.dandadan.dto.response.UniqueSpeedResponse;
import ru.itmo.cs.dandadan.util.Result;

@Remote
public interface GetUniqueImpactSpeedsRemote {
    Result<UniqueSpeedResponse> getUniqueImpactSpeeds();
}
