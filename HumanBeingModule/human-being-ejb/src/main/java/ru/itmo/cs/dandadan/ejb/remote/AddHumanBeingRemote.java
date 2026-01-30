package ru.itmo.cs.dandadan.ejb.remote;

import jakarta.ejb.Local;
import ru.itmo.cs.dandadan.dto.request.HumanBeingRequest;
import ru.itmo.cs.dandadan.dto.response.HumanBeingResponse;
import ru.itmo.cs.dandadan.util.Result;

@Local
public interface AddHumanBeingRemote {
    Result<HumanBeingResponse> addHumanBeing(HumanBeingRequest requestDto);
}
