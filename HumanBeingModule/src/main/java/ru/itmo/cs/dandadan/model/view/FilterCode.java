package ru.itmo.cs.dandadan.model.view;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum FilterCode {
    EQ("eq"),
    NEQ("neq"),
    GT("gt"),
    LT("lt"),
    GTE("gte"),
    LTE("lte"),
    LIKE("like"),
    UNDEFINED("undefined");

    @Getter
    private final String value;

    public String toString() {
        return value;
    }

    public static FilterCode fromValue(String value) {
        return Arrays.stream(FilterCode.values())
                .filter(o -> Objects.equals(o.getValue(), value))
                .findFirst()
                .orElse(UNDEFINED);
    }
}
