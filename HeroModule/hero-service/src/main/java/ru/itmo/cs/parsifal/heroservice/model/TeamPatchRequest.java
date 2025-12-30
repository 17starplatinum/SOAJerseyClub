package ru.itmo.cs.parsifal.heroservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamPatchRequest {
    @NotNull(message = "Human ID cannot be null")
    private Long humanId;

    private Long teamId;

    @NotNull(message = "Operation type cannot be null")
    private OperationType operation;
}