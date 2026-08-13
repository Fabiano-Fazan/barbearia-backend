package com.barbearia.shared.domain.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    protected BusinessException(String message, HttpStatus status) {
        super(message);

        this.status = status;
    }
}
