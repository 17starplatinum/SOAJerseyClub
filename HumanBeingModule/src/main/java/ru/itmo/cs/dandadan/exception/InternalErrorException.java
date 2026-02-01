package ru.itmo.cs.dandadan.exception;

@HttpStatus(500)
public class InternalErrorException extends RuntimeException {
    public InternalErrorException() {
        super("Internal server error: unexpected failure while processing the request");
    }
}
