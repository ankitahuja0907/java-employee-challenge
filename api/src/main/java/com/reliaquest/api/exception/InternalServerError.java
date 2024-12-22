package com.reliaquest.api.exception;

import com.reliaquest.api.util.Constants;

public class InternalServerError extends BaseException {

    public InternalServerError(int statusCode) {
        this.status = statusCode;
        this.message = Constants.MESSAGES.INTERNAL_SERVER_ERROR_OCCURRED_MESSAGE;
    }
}
