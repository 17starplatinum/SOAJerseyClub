package ru.itmo.cs.dandadan.exception;

@HttpStatus(404)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String prefix, String field, String value) {
        super(prefix + "Human being with " + field + "=" + value + " not found");
    }
}
