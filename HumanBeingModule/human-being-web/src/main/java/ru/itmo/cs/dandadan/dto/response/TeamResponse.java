package ru.itmo.cs.dandadan.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamResponse {
    private Long id;
    private String name;
    private int size;
}
