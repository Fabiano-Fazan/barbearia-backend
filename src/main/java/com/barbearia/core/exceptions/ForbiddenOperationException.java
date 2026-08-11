package com.barbearia.core.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenOperationException extends BusinessException {
    public ForbiddenOperationException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
