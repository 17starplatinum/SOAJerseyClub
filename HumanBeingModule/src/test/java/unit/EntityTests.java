package unit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.itmo.cs.dandadan.model.entity.Coordinates;
import ru.itmo.cs.dandadan.model.entity.HumanBeing;
import ru.itmo.cs.dandadan.model.entity.Mood;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
        assertViolation(violations, "name", "Поле 'name' не должно быть пустым");
    }

    @Test
    void name_blank_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        humanBeing.setName("");

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "name", "Поле 'name' не должно быть пустым");
    }

    @Test
    void coordinates_null_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        humanBeing.setCoordinates(null);

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "coordinates", "Поле 'coordinates' не должно быть null");
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
        assertViolation(violations, "hasToothpick", "Поле 'hasToothpick' не должно быть null");
    }

    @Test
    void mood_null_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        humanBeing.setMood(null);

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "mood", "Поле 'mood' не может быть null");
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
        humanBeing.setImpactSpeed(59);

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "impactSpeed", "Поле 'impactSpeed' не должно быть больше 58");
    }

    @Test
    void teamId_nonPositive_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        humanBeing.setTeamId(0L);

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "teamId", "Поле 'teamId' должно быть натуральным числом");
    }

    @Test
    void embedded_coordinates_shouldFailValidation() {
        HumanBeing humanBeing = createValidHumanBeing();
        new Coordinates();
        Coordinates invalidCoordinates = Coordinates.builder()
                .x(-63)
                .y(12.05454)
                .build();
        humanBeing.setCoordinates(invalidCoordinates);

        Set<ConstraintViolation<HumanBeing>> violations = validator.validate(humanBeing);
        assertViolation(violations, "x", "Поле 'x' должно быть больше -63");
    }

    private HumanBeing createValidHumanBeing() {
        return HumanBeing.builder()
                .name("")
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
