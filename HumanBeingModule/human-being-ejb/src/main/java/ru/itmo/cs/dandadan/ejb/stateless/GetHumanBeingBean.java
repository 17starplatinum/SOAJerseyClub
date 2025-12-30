package ru.itmo.cs.dandadan.ejb.stateless;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import ru.itmo.cs.dandadan.dto.response.HumanBeingResponse;
import ru.itmo.cs.dandadan.ejb.remote.GetHumanBeingRemote;
import ru.itmo.cs.dandadan.exception.CustomBadRequestException;
import ru.itmo.cs.dandadan.mapper.HumanBeingMapper;
import ru.itmo.cs.dandadan.repository.api.HumanBeingRepository;
import ru.itmo.cs.dandadan.util.Result;

@Stateless
public class GetHumanBeingBean implements GetHumanBeingRemote {
    @Inject
    private HumanBeingRepository humanBeingRepository;

    @Inject
    private HumanBeingMapper humanBeingMapper;

    @Override
    public Result<HumanBeingResponse> getHumanBeing(Long id) {
        try {
            if (id == null || id < 1) {
                throw new CustomBadRequestException("id", "positive integer", String.valueOf(id));
            }
            return new Result.Success<>(humanBeingMapper.toHumanBeingResponse(humanBeingRepository.getHumanBeing(id)));
        } catch (Exception e) {
            return new Result.Failure<>(e);
        }
    }
}
