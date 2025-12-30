package ru.itmo.cs.dandadan.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.itmo.cs.dandadan.validation.annotation.ValidImpactSpeed;

public class IntegerValidator implements ConstraintValidator<ValidImpactSpeed, Number> {
    private static final long MAX_ALLOWED = 58;
    private static final long MIN_ALLOWED = 0;

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        int intValue = value.intValue();
        if (intValue > MAX_ALLOWED) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "'impactSpeed' maximum value is 58, got " + intValue + ". Possible underflow"
            ).addConstraintViolation();
            return false;
        }
        if (intValue < MIN_ALLOWED) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "'impactSpeed' minimum value is 0, got " + intValue + ". Possible overflow"
            ).addConstraintViolation();
            return false;
        }
        return true;
    }
}
