package ru.itmo.cs.dandadan.unit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.itmo.cs.dandadan.dto.request.HumanBeingRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DtoTests {
    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validHumanBeingRequest_shouldPassValidation() {
        HumanBeingRequest humanBeingRequest = createValidHumanBeingRequest();
        Set<ConstraintViolation<HumanBeingRequest>> violations = validator.validate(humanBeingRequest);
        assertTrue(violations.isEmpty(), "Expected no violations, but found: " + getViolationMessages(violations));
    }

    @Test
    void name_null_shouldFailValidation() {
        HumanBeingRequest humanBeingRequest = createValidHumanBeingRequest();
        humanBeingRequest.setName(null);

        Set<ConstraintViolation<HumanBeingRequest>> violations = validator.validate(humanBeingRequest);
        assertViolation(violations, "name", "'name' cannot be empty");
    }

    @Test
    void name_empty_shouldFailValidation() {
        HumanBeingRequest humanBeingRequest = createValidHumanBeingRequest();
        humanBeingRequest.setName("      ");

        Set<ConstraintViolation<HumanBeingRequest>> violations = validator.validate(humanBeingRequest);
        assertViolation(violations, "name", "'name' cannot be empty");
    }

    @Test
    void coordinates_null_shouldFailValidation() {
        HumanBeingRequest humanBeingRequest = createValidHumanBeingRequest();
        humanBeingRequest.setCoordinates(null);

        Set<ConstraintViolation<HumanBeingRequest>> violations = validator.validate(humanBeingRequest);
        assertViolation(violations, "coordinates", "'coordinates' cannot be null");
    }

    @Test
    void realHero_null_shouldFailValidation() {
        HumanBeingRequest humanBeingRequest = createValidHumanBeingRequest();
        humanBeingRequest.setRealHero(null);

        Set<ConstraintViolation<HumanBeingRequest>> violations = validator.validate(humanBeingRequest);
        assertViolation(violations, "realHero", "'realHero' cannot be null");
    }

    @Test
    void hasToothpick_null_shouldFailValidation() {
        HumanBeingRequest humanBeingRequest = createValidHumanBeingRequest();
        humanBeingRequest.setHasToothpick(null);

        Set<ConstraintViolation<HumanBeingRequest>> violations = validator.validate(humanBeingRequest);
        assertViolation(violations, "hasToothpick", "'hasToothpick' cannot be null");
    }

    @Test
    void mood_null_shouldFailValidation() {
        HumanBeingRequest humanBeingRequest = createValidHumanBeingRequest();
        humanBeingRequest.setMood(null);

        Set<ConstraintViolation<HumanBeingRequest>> violations = validator.validate(humanBeingRequest);
        assertViolation(violations, "mood", "'mood' cannot be null");
    }

    @Test
    void impactSpeed_null_shouldFailValidation() {
        HumanBeingRequest humanBeingRequest = createValidHumanBeingRequest();
        humanBeingRequest.setImpactSpeed(null);

        Set<ConstraintViolation<HumanBeingRequest>> violations = validator.validate(humanBeingRequest);
        assertViolation(violations, "impactSpeed", "'impactSpeed' cannot be null");
    }

    @Test
    void impactSpeed_maxValue_shouldFailValidation() {
        HumanBeingRequest humanBeingRequest = createValidHumanBeingRequest();
        int intValue = 60;
        humanBeingRequest.setImpactSpeed(intValue);

        Set<ConstraintViolation<HumanBeingRequest>> violations = validator.validate(humanBeingRequest);
        assertViolation(violations, "impactSpeed", "'impactSpeed' maximum value is 58, got " + intValue + ". Possible underflow");
    }

    @Test
    void minValue_shouldFailValidation() {
        HumanBeingRequest humanBeingRequest = createValidHumanBeingRequest();
        int intValue = -351354;
        humanBeingRequest.setImpactSpeed(intValue);

        Set<ConstraintViolation<HumanBeingRequest>> violations = validator.validate(humanBeingRequest);
        assertViolation(violations, "impactSpeed", "'impactSpeed' minimum value is 0, got " + intValue + ". Possible overflow");
    }

    @Test
    void teamId_nonPositive_shouldFailValidation() {
        HumanBeingRequest humanBeingRequest = createValidHumanBeingRequest();
        Long invalidTeamId = -1L;
        humanBeingRequest.setTeamId(invalidTeamId);

        Set<ConstraintViolation<HumanBeingRequest>> violations = validator.validate(humanBeingRequest);
        assertViolation(violations, "teamId", "expected a positive integer, got " + invalidTeamId);
    }

    @Test
    void embedded_coordinates_shouldFailValidation() {
        HumanBeingRequest humanBeingRequest = createValidHumanBeingRequest();
        Integer intValue = -66;
        HumanBeingRequest.CoordinatesRequest invalidCoordinatesRequest = new HumanBeingRequest.CoordinatesRequest(intValue, 56.02112);
        humanBeingRequest.setCoordinates(invalidCoordinatesRequest);

        Set<ConstraintViolation<HumanBeingRequest.CoordinatesRequest>> violations = validator.validate(humanBeingRequest.getCoordinates());
        assertViolation(violations, "coordinates.x", "'coordinates.x' exclusive minimum value is -63, got " + intValue);
    }

    @Test
    void embedded_coordinates_x_null_shouldFailValidation() {
        HumanBeingRequest humanBeingRequest = createValidHumanBeingRequest();
        HumanBeingRequest.CoordinatesRequest invalidCoordinatesRequest = new HumanBeingRequest.CoordinatesRequest(null, 795123.4);
        humanBeingRequest.setCoordinates(invalidCoordinatesRequest);

        Set<ConstraintViolation<HumanBeingRequest.CoordinatesRequest>> violations = validator.validate(humanBeingRequest.getCoordinates());
        assertViolation(violations, "x", "'coordinates.x' cannot be null");
    }

    @Test
    void embedded_coordinates_y_null_shouldFailValidation() {
        HumanBeingRequest humanBeingRequest = createValidHumanBeingRequest();
        HumanBeingRequest.CoordinatesRequest invalidCoordinatesRequest = new HumanBeingRequest.CoordinatesRequest(12145, null);
        humanBeingRequest.setCoordinates(invalidCoordinatesRequest);

        Set<ConstraintViolation<HumanBeingRequest.CoordinatesRequest>> violations = validator.validate(humanBeingRequest.getCoordinates());
        assertViolation(violations, "y", "'coordinates.y' cannot be null");
    }

    @Test
    void embedded_car_shouldFailValidation() {
        HumanBeingRequest humanBeingRequest = createValidHumanBeingRequest();
        HumanBeingRequest.CarRequest invalidCarRequest = new HumanBeingRequest.CarRequest(null, null, null);
        humanBeingRequest.setCar(invalidCarRequest);
        Set<ConstraintViolation<HumanBeingRequest.CarRequest>> violations = validator.validate(humanBeingRequest.getCar());
        assertViolation(violations, "color", "'car.color' cannot be null");
    }

    private HumanBeingRequest createValidHumanBeingRequest() {
        return new HumanBeingRequest
                ("RedGry",
                        new HumanBeingRequest.CoordinatesRequest(12, -0.152454),
                        true, true, 10, null, null, "SORROW",
                        new HumanBeingRequest.CarRequest(
                                true, "RED", "Lada Kalina"
                        ));
    }

    private <T> void assertViolation(Set<ConstraintViolation<T>> violations, String field, String expectedMessage) {
        boolean found = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(field) &&
                        v.getMessage().contains(expectedMessage));
        assertTrue(found, "Expected violation for field '" + field +
                "' with message containing '" + expectedMessage + "', but found:" + getViolationMessages(violations));

    }

    private <T> String getViolationMessages(Set<ConstraintViolation<T>> violations) {
        return violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .reduce("", (a, b) -> a + "\n" + b);
    }
}
