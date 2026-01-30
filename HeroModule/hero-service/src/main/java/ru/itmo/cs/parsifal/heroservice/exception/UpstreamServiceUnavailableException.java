package ru.itmo.cs.parsifal.heroservice.exception;

public class UpstreamServiceUnavailableException extends RuntimeException {
    public UpstreamServiceUnavailableException(String message) {
        super(message);
    }

    public UpstreamServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}