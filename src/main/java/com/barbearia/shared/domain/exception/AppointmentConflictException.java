package com.barbearia.shared.domain.exception;

import org.springframework.http.HttpStatus;

public class AppointmentConflictException extends BusinessException {
    public AppointmentConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
