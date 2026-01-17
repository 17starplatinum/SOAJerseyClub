package ru.itmo.cs.dandadan.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum Mood implements Serializable {
    SADNESS("sadness"),
    SORROW("sorrow"),
    GLOOM("gloom"),
    APATHY("apathy"),
    RAGE("rage");

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final String value;

    @Override
    public String toString() {
        return this.value;
    }

    public static Mood fromValue(String value) {
        return Arrays.stream(Mood.values())
                .filter(s -> Objects.equals(s.value, value.toLowerCase()))
                .findFirst()
                .orElse(null);
    }
}
