package ru.itmo.cs.dandadan.mapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.cs.dandadan.dto.response.ErrorResponse;
import ru.itmo.cs.dandadan.exception.HttpStatus;

@Provider
public class HumanBeingExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable ex) {
        Class<?> cls = ex.getClass();
        HttpStatus a = cls.getAnnotation(HttpStatus.class);
        int status = a != null ? a.value() : 400;
        ErrorResponse response = new ErrorResponse(status, ex.getMessage());
        return Response.status(status).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
