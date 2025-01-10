package com.mindhub.todolist.validations;

import com.mindhub.todolist.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueEmailValidation implements ConstraintValidator<UniqueEmail, String> {

    private final UserRepository userRepository;

    @Autowired
    public UniqueEmailValidation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return !userRepository.existsByEmail(email);
    }
}
