package ru.itmo.cs.parsifal.heromodule.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {
    @NotNull(message = "Team name cannot be null")
    @Size(min = 1, message = "Team name must not be empty")
    private String name;

    private Integer size;
}