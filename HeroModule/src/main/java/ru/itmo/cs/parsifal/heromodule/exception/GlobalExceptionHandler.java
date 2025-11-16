package ru.itmo.cs.parsifal.heromodule.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.itmo.cs.parsifal.heromodule.model.ErrorResponse;

import jakarta.validation.ConstraintViolationException;
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
                        return String.format("%s: %s", ((FieldError) error).getField(), error.getDefaultMessage());
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
                .map(violation -> String.format("%s: %s",
                        violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining("; "));

        ErrorResponse error = new ErrorResponse(400, "Constraint violation: " + errorMessage, ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("Parameter '%s' has invalid value: expected %s, got '%s'",
                ex.getName(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName(), ex.getValue());

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
        ErrorResponse error = new ErrorResponse(400, ex.getMessage(), ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        ErrorResponse error = new ErrorResponse(409, "Data integrity violation: " + ex.getMostSpecificCause().getMessage(), ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse();

        if (ex.getMessage().contains("not found")) {
            error.setCode(404);
            error.setMessage(ex.getMessage());
        } else if (ex.getMessage().contains("Invalid") || ex.getMessage().contains("Validation") ||
                ex.getMessage().contains("expected") || ex.getMessage().contains("must")) {
            error.setCode(400);
            error.setMessage(ex.getMessage());
        } else if (ex.getMessage().contains("Upstream")) {
            error.setCode(502);
            error.setMessage("Upstream service error: " + ex.getMessage());
        } else if (ex.getMessage().contains("timeout") || ex.getMessage().contains("Timeout")) {
            error.setCode(504);
            error.setMessage("Upstream timeout: " + ex.getMessage());
        } else if (ex.getMessage().contains("unavailable") || ex.getMessage().contains("Unavailable")) {
            error.setCode(503);
            error.setMessage("Service unavailable: " + ex.getMessage());
        } else {
            error.setCode(500);
            error.setMessage("Internal server error: " + ex.getMessage());
        }

        error.setTime(ZonedDateTime.now());
        return ResponseEntity.status(error.getCode()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse error = new ErrorResponse(500, "Internal server error: " + ex.getMessage(), ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}