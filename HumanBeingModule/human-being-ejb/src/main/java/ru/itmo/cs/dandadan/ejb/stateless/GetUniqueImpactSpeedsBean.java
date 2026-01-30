package ru.itmo.cs.dandadan.ejb.stateless;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import ru.itmo.cs.dandadan.dto.response.UniqueSpeedResponse;
import ru.itmo.cs.dandadan.ejb.remote.GetUniqueImpactSpeedsRemote;
import ru.itmo.cs.dandadan.repository.api.HumanBeingRepository;
import ru.itmo.cs.dandadan.util.Result;

@Stateless
public class GetUniqueImpactSpeedsBean implements GetUniqueImpactSpeedsRemote {
    @Inject
    private HumanBeingRepository humanBeingRepository;

    @Override
    public Result<UniqueSpeedResponse> getUniqueImpactSpeeds() {
        try {
            int[] uniqueSpeeds = humanBeingRepository.getUniqueImpactSpeeds()
                    .stream().mapToInt(Integer::intValue).toArray();
            return new Result.Success<>(new UniqueSpeedResponse(uniqueSpeeds));
        } catch (Exception e) {
            return new Result.Failure<>(e);
        }
    }
}
