package com.barbearia.shared.exceptions;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends BusinessException {
    public EntityAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
