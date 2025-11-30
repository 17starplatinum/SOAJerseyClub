package ru.itmo.cs.dandadan.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.itmo.cs.dandadan.validation.annotation.ValidEnum;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<ValidEnum, Object> {
    private Set<String> enumNames = new HashSet<>();
    private boolean ignoreCase;
    private boolean nullable;
    private String messageTemplate;
    private String validValuesString;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.ignoreCase = constraintAnnotation.ignoreCase();
        this.nullable = constraintAnnotation.nullable();
        this.messageTemplate = constraintAnnotation.message();

        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
        Enum<?>[] enumConstants = enumClass.getEnumConstants();

        this.validValuesString = Arrays.stream(enumConstants)
                .map(Enum::name)
                .collect(Collectors.joining(", "));

        this.enumNames = Arrays.stream(enumConstants)
                .map(e -> ignoreCase ? e.name().toLowerCase() : e.name())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return nullable;
        }

        String enumName;

        if (value instanceof Enum) {
            enumName = ((Enum<?>) value).name();
        } else if (value instanceof String) {
            if (((String) value).trim().isEmpty()) {
                return false;
            }
            enumName = (String) value;
        } else {
            return false;
        }

        String testValue = ignoreCase ? enumName.toLowerCase() : enumName;
        boolean isValid = enumNames.contains(testValue);

        if (!isValid && context != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    messageTemplate.replaceAll("\\$\\{validValues}", validValuesString) +
                            ". Provided value: '" + value + "'"
            ).addConstraintViolation();
        }

        return isValid;
    }
}
