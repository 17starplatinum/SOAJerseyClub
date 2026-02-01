package ru.itmo.cs.dandadan.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.itmo.cs.dandadan.validation.annotation.ValidImpactSpeed;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HumanBeingRequest {

    @NotBlank(message = "'name' cannot be empty")
    private String name;

    @NotNull(message = "'coordinates' cannot be null")
    private CoordinatesRequest coordinates;

    @NotNull(message = "'realHero' cannot be null")
    private Boolean realHero;

    @NotNull(message = "'hasToothpick' cannot be null")
    private Boolean hasToothpick;

    @ValidImpactSpeed
    @NotNull(message = "'impactSpeed' cannot be null")
    private Integer impactSpeed;

    private String weaponType;

    @Min(value = 1, message = "expected a positive integer, got ${validatedValue}")
    private Long teamId;

    @NotNull(message = "'mood' cannot be null")
    private String mood;

    public CarRequest car;

    @Data
    @AllArgsConstructor
    public static class CoordinatesRequest {
        @DecimalMin(value = "-63", inclusive = false, message = "'coordinates.x' exclusive minimum value is -63, got ${validatedValue}")
        @NotNull(message = "'coordinates.x' cannot be null")
        private Integer x;
        @NotNull(message = "'coordinates.y' cannot be null")
        private Double y;
    }

    @Data
    @ToString
    @AllArgsConstructor
    public static class CarRequest {
        private Boolean cool;
        @NotNull(message = "'car.color' cannot be null")
        private String color;
        private String model;
    }
}
