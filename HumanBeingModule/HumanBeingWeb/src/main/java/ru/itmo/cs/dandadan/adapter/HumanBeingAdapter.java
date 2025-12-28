package ru.itmo.cs.dandadan.adapter;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import ru.itmo.cs.dandadan.dto.*;
import ru.itmo.cs.dandadan.dto.request.HumanBeingRequest;
import ru.itmo.cs.dandadan.dto.response.HumanBeingResponse;
import ru.itmo.cs.dandadan.dto.response.UniqueSpeedResponse;
import ru.itmo.cs.dandadan.ejb.remote.*;
import ru.itmo.cs.dandadan.model.view.Page;
import ru.itmo.cs.dandadan.util.Result;

import java.util.List;
import java.util.function.Supplier;

@ApplicationScoped
public class HumanBeingAdapter {
    @EJB(lookup = "java:global/HumanBeingEJB/AddHumanBeingBean!ru.itmo.cs.dandadan.ejb.remote.AddHumanBeingRemote")
    private AddHumanBeingRemote addHumanBeingService;

    @EJB(lookup = "java:global/HumanBeingEJB/DeleteHumanBeingBean!ru.itmo.cs.dandadan.ejb.remote.DeleteHumanBeingRemote")
    private DeleteHumanBeingRemote deleteHumanBeingService;

    @EJB(lookup = "java:global/HumanBeingEJB/GetHumanBeingBean!ru.itmo.cs.dandadan.ejb.remote.GetHumanBeingRemote")
    private GetHumanBeingRemote getHumanBeingService;

    @EJB(lookup = "java:global/HumanBeingEJB/GetHumanBeingsBean!ru.itmo.cs.dandadan.ejb.remote.GetHumanBeingsRemote")
    private GetHumanBeingsRemote getHumanBeingsService;

    @EJB(lookup = "java:global/HumanBeingEJB/GetUniqueImpactSpeedsBean!ru.itmo.cs.dandadan.ejb.remote.GetUniqueImpactSpeedsRemote")
    private GetUniqueImpactSpeedsRemote getUniqueImpactSpeedsService;

    @EJB(lookup = "java:global/HumanBeingEJB/UpdateHumanBeingBean!ru.itmo.cs.dandadan.ejb.remote.UpdateHumanBeingRemote")
    private UpdateHumanBeingRemote updateHumanBeingService;

    private <T> T unwrap(Supplier<Object> supplier) {
        Object obj = supplier.get();
        if (obj instanceof Result.Success) {
            Result.Success<T> success = (Result.Success<T>) obj;
            return success.getValue();
        } else if (obj instanceof Result.Failure) {
            Result.Failure<T> failure = (Result.Failure<T>) obj;
            throw unwrapException(failure.getException());
        }
        throw new IllegalStateException("Unexpected result type: " + obj);
    }

    private RuntimeException unwrapException(Exception ex) {
        if (ex instanceof RuntimeException) {
            return (RuntimeException) ex;
        }
        return new RuntimeException(ex);
    }

    public HumanBeingResponse addHumanBeing(HumanBeingRequest humanBeingRequest) {
        return unwrap(() -> addHumanBeingService.addHumanBeing(humanBeingRequest));
    }

    public void deleteHumanBeing(Long id) {
        unwrap(() -> deleteHumanBeingService.deleteHumanBeing(id));
    }

    public HumanBeingResponse getHumanBeing(Long id) {
        return unwrap(() -> getHumanBeingService.getHumanBeing(id));
    }

    public Page<HumanBeingResponse> getHumanBeings(
            List<String> sortsList,
            List<String> filtersList,
            Integer page,
            Integer pageSize
    ) {
        return unwrap(() -> getHumanBeingsService.getHumanBeings(sortsList, filtersList, page, pageSize));
    }

    public UniqueSpeedResponse getUniqueImpactSpeeds() {
        return unwrap(() -> getUniqueImpactSpeedsService.getUniqueImpactSpeeds());
    }

    public HumanBeingResponse updateHumanBeing(Long id, HumanBeingRequest humanBeingRequest) {
        return unwrap(() -> updateHumanBeingService.updateHumanBeing(id, humanBeingRequest));
    }
}
