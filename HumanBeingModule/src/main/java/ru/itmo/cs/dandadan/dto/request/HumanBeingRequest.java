package ru.itmo.cs.dandadan.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;
import ru.itmo.cs.dandadan.validation.annotation.ValidImpactSpeed;

@Data
public class HumanBeingRequest {

    @NotBlank(message = "'name' must be non-empty")
    private String name;

    @NotNull(message = "Поле 'coordinates' cannot be null")
    private CoordinatesRequest coordinates;

    @NotNull(message = "Поле 'realHero' cannot be null")
    private Boolean realHero;

    @NotNull(message = "'hasToothpick' cannot be null")
    private Boolean hasToothpick;

    @ValidImpactSpeed
    @Max(value = 58, message = "'impactSpeed' maximum value is 58, got ${impactSpeed}")
    @NotNull(message = "'impactSpeed' cannot be null")
    private Integer impactSpeed;

    private String weaponType;

    @Min(value = 1, message = "'teamId' minimum value is 1, got ${teamId}")
    private Long teamId;

    @NotNull(message = "'mood' cannot be null")
    private String mood;

    public CarRequest car;

    @Data
    public static class CoordinatesRequest {
        @DecimalMin(value = "-63", inclusive = false, message = "'coordinates.x' minimum value is -63, got ${x}")
        @NotNull(message = "'coordinates.x' cannot be null")
        private Integer x;
        @NotNull(message = "'coordinates.y' cannot be null")
        private Double y;
    }

    @Data
    @ToString
    public static class CarRequest {
        private Boolean cool;
        @NotNull(message = "'car.color' cannot be null")
        private String color;
        private String model;
    }
}
