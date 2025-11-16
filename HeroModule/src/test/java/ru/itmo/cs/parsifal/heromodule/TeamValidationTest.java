package ru.itmo.cs.parsifal.heromodule;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.itmo.cs.parsifal.heromodule.model.Team;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TeamValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // проверка валидации при отсутствии названия команды (null)
    @Test
    void whenNameIsNull_thenValidationFails() {
        Team team = new Team();
        team.setId(1L);
        team.setName(null);

        Set<ConstraintViolation<Team>> violations = validator.validate(team);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Team name cannot be null", violations.iterator().next().getMessage());
    }

    // проверка валидации при пустом названии команды
    @Test
    void whenNameIsEmpty_thenValidationFails() {
        Team team = new Team();
        team.setId(1L);
        team.setName("");

        Set<ConstraintViolation<Team>> violations = validator.validate(team);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Team name must not be empty", violations.iterator().next().getMessage());
    }

    // проверка валидации при длинном названии команды (1000 символов)
    @Test
    void whenNameIs1000Characters_thenValidationSucceeds() {
        String longName = "A".repeat(1000);
        Team team = new Team();
        team.setId(1L);
        team.setName(longName);

        Set<ConstraintViolation<Team>> violations = validator.validate(team);
        assertTrue(violations.isEmpty());
    }
}