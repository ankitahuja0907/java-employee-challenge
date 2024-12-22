package com.reliaquest.api.exception;

import com.reliaquest.api.util.Constants;
import org.springframework.http.HttpStatus;

public class TooManyRequestException extends BaseException {

    public TooManyRequestException() {
        this.status = HttpStatus.TOO_MANY_REQUESTS.value();
        this.message = Constants.MESSAGES.TOO_MANY_REQUESTS_MESSAGE;
    }
}
