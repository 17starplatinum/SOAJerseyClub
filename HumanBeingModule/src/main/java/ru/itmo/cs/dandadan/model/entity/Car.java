package ru.itmo.cs.dandadan.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Column(name = "car_cool")
    private Boolean cool;

    @Size(min = 3, max = 6)
    @NotNull(message = "Цвет машины не должно быть null")
    @Enumerated(EnumType.STRING)
    @Column(name = "car_color", nullable = false)
    private Color color;

    @NotBlank(message = "Модель машины не должно быть пустым")
    @Column(name = "car_model")
    private String model;
}
