package ru.itmo.cs.dandadan.ejb.remote;

import jakarta.ejb.Local;
import ru.itmo.cs.dandadan.dto.response.UniqueSpeedResponse;
import ru.itmo.cs.dandadan.util.Result;

@Local
public interface GetUniqueImpactSpeedsRemote {
    Result<UniqueSpeedResponse> getUniqueImpactSpeeds();
}
