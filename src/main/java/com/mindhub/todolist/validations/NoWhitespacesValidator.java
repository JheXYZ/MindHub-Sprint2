package com.mindhub.todolist.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class NoWhitespacesValidator implements ConstraintValidator<NoWhitespaces, String> {

    @Override
    public boolean isValid(String string, ConstraintValidatorContext context) {
        return string == null || !string.contains(" ");
    }
}
