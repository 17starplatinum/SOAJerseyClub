package ru.itmo.cs.dandadan.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ErrorResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private String time;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneId.systemDefault());

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.time = formatter.format(Instant.now());
    }
}
