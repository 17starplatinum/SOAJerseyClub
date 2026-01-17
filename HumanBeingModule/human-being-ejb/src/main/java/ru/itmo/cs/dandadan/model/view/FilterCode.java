package ru.itmo.cs.dandadan.model.view;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum FilterCode implements Serializable {
    EQ("eq"),
    NEQ("neq"),
    GT("gt"),
    LT("lt"),
    GTE("gte"),
    LTE("lte"),
    LIKE("like"),
    UNDEFINED("undefined");

    @Serial
    private static final long serialVersionUID = 1L;
    @Getter
    private final String value;

    public String toString() {
        return value;
    }

    public static FilterCode fromValue(String value) {
        return Arrays.stream(FilterCode.values())
                .filter(o -> Objects.equals(o.getValue(), value.toLowerCase()))
                .findFirst()
                .orElse(UNDEFINED);
    }
}
