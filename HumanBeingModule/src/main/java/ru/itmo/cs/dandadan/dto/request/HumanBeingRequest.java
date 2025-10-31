package ru.itmo.cs.dandadan.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

@Data
public class HumanBeingRequest {

    @NotNull(message = "Поле 'name' не должно быть null")
    @NotBlank(message = "Поле 'name' не должно быть пустым")
    private String name;

    @NotNull(message = "Поле 'coordinates' не должно быть null")
    private CoordinatesAddRequestDto coordinates;

    @NotNull(message = "Поле 'realHero' не должно быть null")
    private boolean realHero;

    @NotNull(message = "Поле 'hasToothpick' не должно быть null")
    private Boolean hasToothpick;

    @Max(value = 58, message = "Coordinates X не должен быть больше 58")
    @NotNull(message = "Поле 'impactSpeed' не должно быть null")
    private int impactSpeed;

    private String weaponType;

    @Min(value = 1, message = "Поле 'teamId' является идентификатором")
    private Long teamId;

    @NotNull(message = "Поле 'mood' не может быть null")
    private String mood;

    public CarGetRequestDto car;

    @Data
    public static class CoordinatesAddRequestDto {
        @DecimalMin(value = "-63", inclusive = false, message = "Coordinates X не должен быть меньше или равно -63")
        @NotNull(message = "Coordinates X не может быть null")
        private Integer x;
        @NotNull(message = "Coordinates Y не может быть null")
        private Double y;
    }

    @Data
    @ToString
    public static class CarGetRequestDto {
        private Boolean cool;

        @Size(min = 3, max = 6)
        @NotNull(message = "Цвет машины не должно быть null")
        private String color;

        @NotBlank(message = "Модель машины не должно быть пустым")
        private String model;
    }
}
