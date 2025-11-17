package ru.itmo.cs.dandadan.exception;

import ru.itmo.cs.dandadan.dto.response.Event;

public class ConflictException extends RuntimeException {
    public ConflictException(Event event, Long teamId) {
        super("Cannot " + event.name().toLowerCase() + " this humanBeing to a team with id=" + teamId);
    }
}
