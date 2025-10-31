package ru.itmo.cs.dandadan.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamRequest {
    @NotNull
    private Long humanId;
    private Long teamId;
    @NotNull
    private Event operation;
}
