package ru.itmo.cs.parsifal.heromodule.exception;

public class UpstreamBadGatewayException extends RuntimeException {
    public UpstreamBadGatewayException(String message) {
        super(message);
    }

    public UpstreamBadGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}