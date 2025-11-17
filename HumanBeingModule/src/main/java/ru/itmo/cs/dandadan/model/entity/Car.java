package ru.itmo.cs.dandadan.model.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;

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

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "car_color")
    private Color color = Color.UNDEFINED;

    @Column(name = "car_model")
    private String model;
}
