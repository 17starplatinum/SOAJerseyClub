package ru.itmo.cs.parsifal.heroservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamPaginatedResponse {
    private List<TeamResponse> teams;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
    private Integer totalCount;
}