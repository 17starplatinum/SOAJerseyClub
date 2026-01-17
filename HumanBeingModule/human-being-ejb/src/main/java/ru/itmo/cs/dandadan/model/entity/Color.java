package ru.itmo.cs.dandadan.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum Color implements Serializable {
    RED("red"),
    BLUE("blue"),
    YELLOW("yellow"),
    GREEN("green"),
    BLACK("black"),
    WHITE("white");

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final String value;

    public static Color fromValue(String value){
        return Arrays.stream(Color.values())
                .filter(e -> Objects.equals(e.value, value.toLowerCase()))
                .findFirst()
                .orElse(null);
    }
}
