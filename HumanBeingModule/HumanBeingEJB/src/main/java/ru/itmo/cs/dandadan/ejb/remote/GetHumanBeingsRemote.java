package ru.itmo.cs.dandadan.ejb.remote;

import jakarta.ejb.Remote;
import ru.itmo.cs.dandadan.dto.response.HumanBeingResponse;
import ru.itmo.cs.dandadan.model.view.Page;
import ru.itmo.cs.dandadan.util.Result;

import java.util.List;

@Remote
public interface GetHumanBeingsRemote {
    Result<Page<HumanBeingResponse>> getHumanBeings(
            List<String> sortsList,
            List<String> filtersList,
            Integer page,
            Integer pageSize
    );
}
