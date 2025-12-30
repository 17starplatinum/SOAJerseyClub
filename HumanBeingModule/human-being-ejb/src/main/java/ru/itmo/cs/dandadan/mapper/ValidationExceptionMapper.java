package ru.itmo.cs.dandadan.mapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneId.systemDefault());
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, List<String>> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String field = getFieldFromPath(violation.getPropertyPath());
            String message = violation.getMessage();

            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
        }
        Map<String, ? super Object> response = new LinkedHashMap<>();
        response.put("code", 422);
        response.put("message", "Validation failed for " + errors.size() + " field(s)");
        response.put("errors", errors);
        response.put("time", formatter.format(Instant.now()));

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(response).build();
    }

    private String getFieldFromPath(Path path) {
        return StreamSupport.stream(path.spliterator(), false)
                .map(Path.Node::getName)
                .filter(name -> !name.equals("arg0") && !name.equals("value"))
                .collect(Collectors.joining("."));
    }
}
