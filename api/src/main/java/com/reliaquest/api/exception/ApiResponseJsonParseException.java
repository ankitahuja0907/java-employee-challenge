package com.reliaquest.api.exception;

import com.reliaquest.api.util.Constants;
import org.springframework.http.HttpStatus;

public class ApiResponseJsonParseException extends BaseException {

    public ApiResponseJsonParseException() {
        this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.message = Constants.MESSAGES.JSON_PARSING_EXCEPTION_MESSAGE;
    }
}
