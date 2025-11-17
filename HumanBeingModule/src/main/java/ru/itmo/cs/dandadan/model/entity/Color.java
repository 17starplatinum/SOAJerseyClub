package ru.itmo.cs.dandadan.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.itmo.cs.dandadan.exception.CustomBadRequestException;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum Color {
    RED("red"),
    BLUE("blue"),
    YELLOW("yellow"),
    GREEN("green"),
    BLACK("black"),
    WHITE("white"),
    UNDEFINED("undefined");

    @Getter
    private final String value;

    public static Color fromValue(String value){
        return Arrays.stream(Color.values())
                .filter(e -> Objects.equals(e.getValue(), value.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new CustomBadRequestException("color", "color should be one of the following: RED, BLUE, YELLOW, GREEN, BLACK, WHITE, or undefined"));
    }
}
