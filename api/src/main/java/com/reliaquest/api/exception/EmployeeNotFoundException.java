package com.reliaquest.api.exception;

import com.reliaquest.api.util.Constants;
import org.springframework.http.HttpStatus;

public class EmployeeNotFoundException extends BaseException {

    private static final long serialVersionUID = 1L;

    public EmployeeNotFoundException(String message) {
        this.status = HttpStatus.NOT_FOUND.value();
        this.message = Constants.MESSAGES.EMPLOYEE_NOT_FOUND_WITH_ID_MESSAGE + message;
    }
}
