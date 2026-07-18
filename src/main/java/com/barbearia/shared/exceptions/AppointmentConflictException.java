package com.barbearia.shared.exceptions;

import org.springframework.http.HttpStatus;

public class AppointmentConflictException extends BusinessException {
    public AppointmentConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
