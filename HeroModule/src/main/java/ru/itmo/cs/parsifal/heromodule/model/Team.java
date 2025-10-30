package ru.itmo.cs.parsifal.heromodule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Team name cannot be null")
    @Size(min = 1, message = "Team name must not be empty")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Team size cannot be null")
    @Min(value = 2, message = "Team size must be at least 2")
    @Column(nullable = false)
    private Integer size;
}