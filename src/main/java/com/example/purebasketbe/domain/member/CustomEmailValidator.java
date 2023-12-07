package com.example.purebasketbe.domain.member;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomEmailValidator implements ConstraintValidator<CustomEmail, String> {
    public void initialize(CustomEmail constraint) {
    }

    public boolean isValid(String emailField, ConstraintValidatorContext context) {
        return emailField != null && emailField.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
    }
}