package ru.itmo.cs.parsifal.heroservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.itmo.cs.parsifal.heroservice.model.ErrorResponse;

import javax.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        return String.format("'%s': %s", ((FieldError) error).getField(), error.getDefaultMessage());
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining("; "));

        ErrorResponse error = new ErrorResponse(422, "Validation failed: " + errorMessage, ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(violation -> String.format("'%s': %s",
                        violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining("; "));

        ErrorResponse error = new ErrorResponse(400, "Constraint violation: " + errorMessage, ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage;
        String paramName = ex.getName();

        if ("real-hero-only".equals(paramName)) {
            errorMessage = String.format("Invalid 'real-hero-only' value: expected boolean (true|false), got '%s'",
                    ex.getValue());
        } else if ("team-id".equals(paramName) || "id".equals(paramName)) {
            errorMessage = String.format("Invalid '%s': expected positive integer, got '%s'",
                    paramName, ex.getValue());
        } else {
            errorMessage = String.format("Parameter '%s' has invalid value: expected %s, got '%s'",
                    paramName, Objects.requireNonNull(ex.getRequiredType()).getSimpleName(), ex.getValue());
        }

        ErrorResponse error = new ErrorResponse(400, errorMessage, ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        String errorMessage = String.format("Required parameter '%s' is missing", ex.getParameterName());

        ErrorResponse error = new ErrorResponse(400, errorMessage, ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        String message = ex.getMessage();
        int code = 400;

        if (message != null) {
            if (message.contains("must be a positive integer") || message.contains("must be positive")) {
                code = 400;
            } else if (message.contains("minimum value is") || message.contains("maximum value is")) {
                code = 422;
            }
        }

        ErrorResponse error = new ErrorResponse(code, message, ZonedDateTime.now());
        return ResponseEntity.status(code).body(error);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        ErrorResponse error = new ErrorResponse(409, "Data integrity violation: " + ex.getMostSpecificCause().getMessage(), ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // Специальные исключения для upstream ошибок
    @ExceptionHandler(UpstreamTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleUpstreamTimeout(UpstreamTimeoutException ex) {
        ErrorResponse error = new ErrorResponse(504,
                "Upstream timeout: no response from Human Being service within 5 seconds.",
                ZonedDateTime.now());
        return ResponseEntity.status(504).body(error);
    }

    @ExceptionHandler(UpstreamServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleUpstreamServiceUnavailable(UpstreamServiceUnavailableException ex) {
        ErrorResponse error = new ErrorResponse(503,
                "Upstream unavailable: Human Being service is down or not accepting requests.",
                ZonedDateTime.now());
        return ResponseEntity.status(503).body(error);
    }

    @ExceptionHandler(UpstreamBadGatewayException.class)
    public ResponseEntity<ErrorResponse> handleUpstreamBadGateway(UpstreamBadGatewayException ex) {
        ErrorResponse error = new ErrorResponse(502,
                "Upstream error: Human Being service responded with 5xx while processing the request.",
                ZonedDateTime.now());
        return ResponseEntity.status(502).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        ErrorResponse error = new ErrorResponse(404, ex.getMessage(), ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        ErrorResponse error = new ErrorResponse(422, "Validation failed: " + ex.getMessage(), ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        ErrorResponse error = new ErrorResponse();
        error.setTime(ZonedDateTime.now());

        if (message != null) {
            if (message.contains("not found") || message.contains("not exist")) {
                error.setCode(404);
                error.setMessage(message);
                return ResponseEntity.status(404).body(error);
            } else if (message.contains("Invalid") || message.contains("invalid") ||
                    message.contains("expected") || message.contains("must be")) {
                error.setCode(400);
                error.setMessage(message);
                return ResponseEntity.status(400).body(error);
            } else if (message.contains("Validation failed")) {
                error.setCode(422);
                error.setMessage(message);
                return ResponseEntity.status(422).body(error);
            } else if (message.contains("Upstream timeout") || message.contains("timeout")) {
                error.setCode(504);
                error.setMessage("Upstream timeout: " + message);
                return ResponseEntity.status(504).body(error);
            } else if (message.contains("Upstream service unavailable") ||
                    message.contains("service unavailable")) {
                error.setCode(503);
                error.setMessage("Upstream service unavailable: " + message);
                return ResponseEntity.status(503).body(error);
            } else if (message.contains("Upstream") || message.contains("upstream")) {
                error.setCode(502);
                error.setMessage("Upstream service error: " + message);
                return ResponseEntity.status(502).body(error);
            }
        }

        error.setCode(500);
        error.setMessage("Internal server error: Unexpected error in Hero service while processing the request.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse error = new ErrorResponse(500,
                "Internal server error: Unexpected error in Hero service.",
                ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}