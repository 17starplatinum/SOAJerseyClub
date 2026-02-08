package ru.itmo.cs.dandadan.soap.adapter;


import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeAdapter extends XmlAdapter<String, ZonedDateTime> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public ZonedDateTime unmarshal(String v) throws Exception {
        if (v == null || v.isEmpty()) {
            return null;
        }
        return ZonedDateTime.parse(v, FORMATTER);
    }

    @Override
    public String marshal(ZonedDateTime v) throws Exception {
        if (v == null) {
            return null;
        }
        return v.format(FORMATTER);
    }
}