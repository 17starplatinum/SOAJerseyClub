package ru.itmo.cs.dandadan.model.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.itmo.cs.dandadan.validation.annotation.ValidEnum;

import java.io.Serializable;

@JsonPropertyOrder({"cool", "color", "model"})
@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car implements Serializable {

    @Column(name = "car_cool")
    private Boolean cool;

    @Enumerated(EnumType.STRING)
    @Column(name = "car_color")
    @NotNull(message = "'car.color' cannot be null")
    @ValidEnum(message = "should be one of the following: ${validValues}", enumClass = Color.class, nullable = true)
    private Color color;

    @Column(name = "car_model")
    private String model;
}
