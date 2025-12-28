package ru.itmo.cs.dandadan.ejb.stateless;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.itmo.cs.dandadan.ejb.remote.DeleteHumanBeingRemote;
import ru.itmo.cs.dandadan.exception.CustomBadRequestException;
import ru.itmo.cs.dandadan.repository.api.HumanBeingRepository;
import ru.itmo.cs.dandadan.util.Result;

@Stateless
@Transactional
public class DeleteHumanBeingBean implements DeleteHumanBeingRemote {
    @Inject
    private HumanBeingRepository humanBeingRepository;

    @Override
    public Result<Void> deleteHumanBeing(Long id) {
        try {
            if (id == null || id < 1) {
                throw new CustomBadRequestException("id", "positive integer", String.valueOf(id));
            }
            humanBeingRepository.deleteHumanBeing(id);
            return new Result.Success<>(null);
        } catch (Exception e) {
            return new Result.Failure<>(e);
        }
    }
}
