package ru.itmo.cs.dandadan.util;

import jakarta.ejb.Stateless;
import ru.itmo.cs.dandadan.exception.CustomBadRequestException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;

@Stateless
public class DateTimeConverter {
    public ZonedDateTime parseZonedDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new CustomBadRequestException("Empty datetime value");
        }

        String cleanValue = cleanFilterValue(value);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                    .withZone(ZoneId.systemDefault());

            TemporalAccessor temporal = formatter.parse(cleanValue);
            LocalDateTime dateTime = LocalDateTime.from(temporal);
            ZoneId zone = temporal.query(TemporalQueries.zone());

            if (zone == null) {
                zone = ZoneId.of("UTC");
            }

            return ZonedDateTime.of(dateTime, zone);
        } catch (DateTimeParseException e1) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")
                        .withZone(ZoneId.of("UTC"));
                return ZonedDateTime.parse(cleanValue, formatter);
            } catch (DateTimeParseException e2) {
                throw new CustomBadRequestException("Invalid datetime format. Use 'yyyy-MM-ddTHH:mm:ss.SSSZ'");
            }
        }
    }

    private String cleanFilterValue(String value) {
        if (value == null) return null;
        return value.trim()
                .replaceAll("^\\[|]$", "")
                .replaceAll("^\"|\"$", "")
                .replaceAll("^'|'$", "")
                .trim();
    }
}