package com.reliaquest.api.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

    protected int status;
    protected String message;
}
