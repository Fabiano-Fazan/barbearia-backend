package com.barbearia.shared.domain.exception;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends BusinessException {
    public EntityAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
