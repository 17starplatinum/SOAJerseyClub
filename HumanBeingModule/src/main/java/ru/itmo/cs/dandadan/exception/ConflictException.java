package ru.itmo.cs.dandadan.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(Long teamId) {
        super("Cannot add this humanBeing to a team with id=" + teamId);
    }
}
