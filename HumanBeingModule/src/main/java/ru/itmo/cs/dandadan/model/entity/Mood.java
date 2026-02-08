package ru.itmo.cs.dandadan.model.entity;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@XmlType(name = "mood")
@XmlEnum
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
                .orElse(null);
    }
}
