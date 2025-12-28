package ru.itmo.cs.dandadan.unit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.itmo.cs.dandadan.model.entity.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EntityTests {
    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validHumanBeing_shouldPassValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertTrue(violations.isEmpty(), "Expected no violations, but found: " + getViolationMessages(violations));
    }

    @Test
    void name_null_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        humanBeing.setName(null);

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "name", "'name' cannot be empty");
    }

    @Test
    void name_blank_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        humanBeing.setName("");

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "name", "'name' cannot be empty");
    }

    @Test
    void coordinates_null_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        humanBeing.setCoordinates(null);

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "coordinates", "'coordinates' cannot be null");
    }

    // Тест чисто для покрытия, всм примитив будет null?
    @Test
    void realHero_null_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        humanBeing.setRealHero(false);
    }

    @Test
    void hasToothpick_null_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        humanBeing.setHasToothpick(null);

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "hasToothpick", "'hasToothpick' cannot be null");
    }

    @Test
    void weaponType_invalidValue_shouldBecomeNull() {
        HumanBeing humanBeing = createValidHumanBeing();
        humanBeing.setWeaponType(WeaponType.fromValue("SWORD"));

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertEquals(0, violations.size());
    }

    @Test
    void mood_invalidValue_shouldBecomeNull() {
        HumanBeing humanBeing = createValidHumanBeing();
        humanBeing.setMood(Mood.fromValue("HAPPINESS"));

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "mood", "'mood' cannot be null");
    }

    @Test
    void mood_null_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        humanBeing.setMood(null);

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "mood", "'mood' cannot be null");
    }

    // Тест чисто для покрытия, всм примитив будет null?
    @Test
    void impactSpeed_null_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        humanBeing.setImpactSpeed(0);
    }

    @Test
    void impactSpeed_maxLimit_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        int intValue = 59;
        humanBeing.setImpactSpeed(intValue);

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "impactSpeed", "'impactSpeed' maximum value is 58, got " + intValue + ". Possible underflow");
    }

    @Test
    void impactSpeed_minLimit_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        int intValue = -3;
        humanBeing.setImpactSpeed(intValue);

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "impactSpeed", "'impactSpeed' minimum value is 0, got " + intValue + ". Possible overflow");
    }

    @Test
    void teamId_nonPositive_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        Long invalidTeamId = 0L;
        humanBeing.setTeamId(invalidTeamId);

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "teamId", "expected a positive integer, got " + invalidTeamId);
    }

    @Test
    void embedded_coordinates_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        Integer intValue = -63;
        Coordinates invalidCoordinates = Coordinates.builder()
                .x(intValue)
                .y(12.05454)
                .build();
        humanBeing.setCoordinates(invalidCoordinates);

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "coordinates.x", "'coordinates.x' exclusive minimum value is -63, got " + intValue);
    }

    @Test
    void embedded_car_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        Car invalidCar = Car.builder()
                .cool(null)
                .color(Color.fromValue("CYAN"))
                .model("Grand National Experimental")
                .build();
        humanBeing.setCar(invalidCar);
        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "car.color", "'car.color' cannot be null");
    }

    private HumanBeing createValidHumanBeing() {
        return HumanBeing.builder()
                .name("Redgry")
                .creationDate(ZonedDateTime.now(ZoneId.of("UTC")))
                .realHero(true)
                .hasToothpick(true)
                .impactSpeed(10)
                .mood(Mood.SADNESS)
                .coordinates(new Coordinates(12, -0.152454))
                .build();
    }

    private void assertViolation(Set<ConstraintViolation<HumanBeing>> violations, String field, String expectedMessage) {
        boolean found = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(field) &&
                        v.getMessage().contains(expectedMessage));
        assertTrue(found, "Expected violation for field '" + field +
                "' with message containing '" + expectedMessage + "', but found:" + getViolationMessages(violations));
    }

    private String getViolationMessages(Set<ConstraintViolation<HumanBeing>> violations) {
        return violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .reduce("", (a, b) -> a + "\n" + b);
    }
}
