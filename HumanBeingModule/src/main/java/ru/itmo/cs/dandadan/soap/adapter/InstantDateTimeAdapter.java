package ru.itmo.cs.dandadan.soap.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class InstantDateTimeAdapter extends XmlAdapter<String, Instant> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .withZone(ZoneOffset.UTC);

    @Override
    public Instant unmarshal(String v) {
        if (v == null || v.isEmpty()) {
            return null;
        }
        return Instant.from(FORMATTER.parse(v));
    }

    @Override
    public String marshal(Instant v) {
        if (v == null) {
            return null;
        }
        return FORMATTER.format(v);
    }
}
