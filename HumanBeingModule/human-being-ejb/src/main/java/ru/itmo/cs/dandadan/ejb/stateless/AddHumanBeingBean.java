package ru.itmo.cs.dandadan.ejb.stateless;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.itmo.cs.dandadan.client.HeroServiceClient;
import ru.itmo.cs.dandadan.dto.request.HumanBeingRequest;
import ru.itmo.cs.dandadan.dto.response.HumanBeingResponse;
import ru.itmo.cs.dandadan.ejb.remote.AddHumanBeingRemote;
import ru.itmo.cs.dandadan.exception.CustomBadRequestException;
import ru.itmo.cs.dandadan.exception.ValidationFailedException;
import ru.itmo.cs.dandadan.mapper.HumanBeingMapper;
import ru.itmo.cs.dandadan.model.entity.HumanBeing;
import ru.itmo.cs.dandadan.repository.api.HumanBeingRepository;
import ru.itmo.cs.dandadan.util.Result;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Stateless
@Transactional
public class AddHumanBeingBean implements AddHumanBeingRemote {
    @Inject
    private HumanBeingRepository humanBeingRepository;

    @Inject
    private HumanBeingMapper humanBeingMapper;

    @Inject
    private HeroServiceClient heroServiceClient;

    @Override
    public Result<HumanBeingResponse> addHumanBeing(HumanBeingRequest requestDto) {
        try {
            if (requestDto == null) {
                throw new ValidationFailedException("incoming request is null");
            }
            HumanBeing humanBeing = humanBeingMapper.fromHumanBeingRequest(requestDto);
            Long potentialTeamId = requestDto.getTeamId();
            humanBeing.setCreationDate(ZonedDateTime.now(ZoneId.systemDefault()));
            if (potentialTeamId != null) {
                try {
                    heroServiceClient.manipulateHumanBeingToTeam(potentialTeamId);
                } catch (Exception e) {
                    throw new CustomBadRequestException(e.getMessage());
                }
            }
            humanBeing = humanBeingRepository.saveHumanBeing(humanBeing, potentialTeamId);
            return new Result.Success<>(humanBeingMapper.toHumanBeingResponse(humanBeing));
        } catch (Exception e) {
            return new Result.Failure<>(e);
        }
    }
}
