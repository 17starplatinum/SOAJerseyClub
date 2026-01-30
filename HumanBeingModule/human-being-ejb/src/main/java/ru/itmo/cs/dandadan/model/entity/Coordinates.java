package ru.itmo.cs.dandadan.model.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@JsonPropertyOrder({"x", "y"})
@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates implements Serializable {

    @NotNull(message = "'coordinates.x' cannot be null")
    @Column(name = "coordinates_x", nullable = false)
    @DecimalMin(value = "-63", inclusive = false, message = "'coordinates.x' exclusive minimum value is -63, got ${validatedValue}")
    private Integer x;

    @Column(name = "coordinates_y", nullable = false)
    private double y;
}
