package ru.itmo.cs.dandadan.exception;

@HttpStatus(422)
public class ValidationFailedException extends RuntimeException {
    public ValidationFailedException(String reason) {
        super("Validation failed: " + reason);
    }
}
