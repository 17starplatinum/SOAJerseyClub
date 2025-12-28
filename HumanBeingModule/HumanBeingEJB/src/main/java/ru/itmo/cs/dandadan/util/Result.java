package ru.itmo.cs.dandadan.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @AllArgsConstructor
    public static final class Success<T> extends Result<T> {
        private static final long serialVersionUID = 1L;
        @Getter
        private final T value;
    }

    @AllArgsConstructor
    public static final class Failure<T> extends Result<T> {
        private static final long serialVersionUID = 1L;
        @Getter
        private final Exception exception;
    }
}
