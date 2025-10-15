package ru.itmo.cs.dandadan.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {

    @Column(name = "coordinates_x", nullable = false)
    @DecimalMin(value = "-63", inclusive = false)
    private Integer x;

    @Column(name = "coordinates_y", nullable = false)
    private double y;
}
