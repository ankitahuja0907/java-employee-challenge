package com.reliaquest.api.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseAPIError {

    private int status;
    private String message;
}
