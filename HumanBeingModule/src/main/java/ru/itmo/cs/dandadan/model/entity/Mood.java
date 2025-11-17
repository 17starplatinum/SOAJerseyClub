package ru.itmo.cs.dandadan.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.itmo.cs.dandadan.exception.CustomBadRequestException;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum Mood {
    SADNESS("sadness"),
    SORROW("sorrow"),
    GLOOM("gloom"),
    APATHY("apathy"),
    RAGE("rage");

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
                .orElseThrow(() -> new CustomBadRequestException("mood", "color should be one of the following: SADNESS, SORROW, GLOOM, APATHY, RAGE"));
    }
}
