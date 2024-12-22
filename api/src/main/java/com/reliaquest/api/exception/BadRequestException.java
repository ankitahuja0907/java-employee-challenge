package com.reliaquest.api.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {

    public BadRequestException(String message) {
        this.status = HttpStatus.BAD_REQUEST.value();
        this.message = message;
    }
}
