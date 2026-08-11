package com.barbearia.core.exceptions;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends BusinessException {
    public EntityAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
