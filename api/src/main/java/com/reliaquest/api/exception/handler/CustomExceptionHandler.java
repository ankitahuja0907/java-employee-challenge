package com.reliaquest.api.exception.handler;

import com.reliaquest.api.exception.ApiResponseJsonParseException;
import com.reliaquest.api.exception.BadRequestException;
import com.reliaquest.api.exception.BaseAPIError;
import com.reliaquest.api.exception.BaseException;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.InternalServerError;
import com.reliaquest.api.exception.TooManyRequestException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
        TooManyRequestException.class, EmployeeNotFoundException.class,
        ApiResponseJsonParseException.class, InternalServerError.class,
        BadRequestException.class
    })
    protected ResponseEntity<Object> handleCustomException(BaseException ex) {
        BaseAPIError baseAPIError = new BaseAPIError();
        buildApiError(baseAPIError, ex);
        return buildResponseEntity(baseAPIError);
    }

    private void buildApiError(BaseAPIError baseAPIError, BaseException ex) {
        baseAPIError.setStatus(ex.getStatus());
        baseAPIError.setMessage(ex.getMessage());
    }

    private ResponseEntity<Object> buildResponseEntity(BaseAPIError baseAPIError) {
        return new ResponseEntity<>(baseAPIError, HttpStatus.valueOf(baseAPIError.getStatus()));
    }
}
