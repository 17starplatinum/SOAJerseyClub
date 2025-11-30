package ru.itmo.cs.dandadan.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.itmo.cs.dandadan.validation.IntegerValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = IntegerValidator.class)
@Documented
public @interface ValidImpactSpeed {
    String message() default "Invalid impact speed value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
