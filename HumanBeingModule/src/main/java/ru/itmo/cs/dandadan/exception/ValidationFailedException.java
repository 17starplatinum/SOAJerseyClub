package ru.itmo.cs.dandadan.exception;

@HttpStatus(400)
public class ValidationFailedException extends RuntimeException {
    public ValidationFailedException(String reason) {
        super("Validation failed: " + reason);
    }
}
