package ru.itmo.cs.dandadan.service.api;

import ru.itmo.cs.dandadan.dto.request.HumanBeingRequest;
import ru.itmo.cs.dandadan.dto.response.HumanBeingResponse;
import ru.itmo.cs.dandadan.dto.response.UniqueSpeedResponse;
import ru.itmo.cs.dandadan.model.view.Page;

import java.util.List;

public interface HumanBeingService {
    Page<HumanBeingResponse> getHumanBeings(List<String> sortsList, List<String> filtersList, Integer page, Integer pageSize);

    HumanBeingResponse getHumanBeing(Long id);

    HumanBeingResponse updateHumanBeing(Long id, HumanBeingRequest requestDto);

    HumanBeingResponse addHumanBeing(HumanBeingRequest requestDto);

    void deleteHumanBeing(Long id);

    UniqueSpeedResponse getUniqueImpactSpeeds();
}
