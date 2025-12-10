package ru.itmo.cs.parsifal.heroservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private Integer code;
    private String message;
    private ZonedDateTime time;
}