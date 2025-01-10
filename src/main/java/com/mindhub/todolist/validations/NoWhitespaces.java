package com.mindhub.todolist.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NoWhitespacesValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoWhitespaces {

    String message() default "can not contain whitespaces";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
