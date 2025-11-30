package ru.itmo.cs.parsifal.heromodule.model;

import lombok.Data;
import java.util.List;

@Data
public class HumanBeingPaginatedResponse {
    private List<HumanBeingFullResponse> humanBeingGetResponseDtos;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
    private Integer totalCount;
}