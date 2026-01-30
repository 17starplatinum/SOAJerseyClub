package ru.itmo.cs.dandadan.ejb.stateless;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.itmo.cs.dandadan.client.HeroServiceClient;
import ru.itmo.cs.dandadan.dto.request.HumanBeingRequest;
import ru.itmo.cs.dandadan.dto.response.HumanBeingResponse;
import ru.itmo.cs.dandadan.ejb.remote.UpdateHumanBeingRemote;
import ru.itmo.cs.dandadan.exception.CustomBadRequestException;
import ru.itmo.cs.dandadan.exception.ValidationFailedException;
import ru.itmo.cs.dandadan.mapper.HumanBeingMapper;
import ru.itmo.cs.dandadan.model.entity.HumanBeing;
import ru.itmo.cs.dandadan.repository.api.HumanBeingRepository;
import ru.itmo.cs.dandadan.util.Result;

@Stateless
@Transactional
public class UpdateHumanBeingBean implements UpdateHumanBeingRemote {
    @Inject
    private HumanBeingRepository humanBeingRepository;

    @Inject
    private HumanBeingMapper humanBeingMapper;

    @Inject
    private HeroServiceClient heroServiceClient;

    @Override
    public Result<HumanBeingResponse> updateHumanBeing(Long id, HumanBeingRequest requestDto) {
        try {
            if (id == null || id < 1) {
                throw new CustomBadRequestException("id", "positive integer", String.valueOf(id));
            }
            if (requestDto == null) {
                throw new ValidationFailedException("incoming request is null");
            }
            Long potentialTeamId = requestDto.getTeamId();
            HumanBeing incomingHumanBeing = humanBeingMapper.fromHumanBeingRequest(requestDto);
            if (potentialTeamId != null &&
                    humanBeingRepository.getHumanBeing(id).getTeamId() != null &&
                    !humanBeingRepository.getHumanBeing(id).getTeamId().equals(potentialTeamId)) {
                try {
                    heroServiceClient.manipulateHumanBeingToTeam(potentialTeamId);
                } catch (Exception e) {
                    throw new CustomBadRequestException(e.getMessage());
                }
            }
            incomingHumanBeing = humanBeingRepository.updateHumanBeing(id, incomingHumanBeing);
            return new Result.Success<>(humanBeingMapper.toHumanBeingResponse(incomingHumanBeing));
        } catch (Exception e) {
            return new Result.Failure<>(e);
        }
    }
}
