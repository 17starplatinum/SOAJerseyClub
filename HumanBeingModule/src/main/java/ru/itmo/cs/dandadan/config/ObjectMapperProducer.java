package ru.itmo.cs.dandadan.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import jakarta.enterprise.inject.Produces;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Provider
public class ObjectMapperProducer implements ContextResolver<ObjectMapper> {
    private final ObjectMapper mapper;

    public ObjectMapperProducer() {
        this.mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
        mapper.registerModule(javaTimeModule);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configOverride(ZonedDateTime.class).setFormat(
                JsonFormat.Value.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        );
    }

    @Produces
    public ObjectMapper objectMapper() {
        return mapper;
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}
