package ru.itmo.cs.dandadan.exception;

@HttpStatus(400)
public class CustomBadRequestException extends RuntimeException {
    public CustomBadRequestException() {
        super("Bad Request");
    }

    public CustomBadRequestException(String message) {
        super(message);
    }

    public CustomBadRequestException(String field, String cause) {
        super("Invalid " + field + " value: " + cause);
    }

    public CustomBadRequestException(String field, String expected, String actual) {
        super("Invalid " + field + ": " + "expected a " + expected + ", got " + actual);
    }
}
