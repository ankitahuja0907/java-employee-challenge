package com.reliaquest.api.util;

public interface Constants {
    interface MESSAGES {
        String BAD_INPUT_EXCEPTION_MESSAGE = "Bad Input. Please check once";
        String INTERNAL_SERVER_ERROR_OCCURRED_MESSAGE = "Internal Server Error Occurred While calling API";
        String JSON_PARSING_EXCEPTION_MESSAGE = "Json Parsing Exception while extracting api response";
        String EMPLOYEE_NOT_FOUND_WITH_ID_MESSAGE = "Employee Not found with id ";
        String TOO_MANY_REQUESTS_MESSAGE = "Too Many Requests, Please wait for some time.";
    }

    interface API_PATH_EMPLOYEE {
        String BASE = "/api/v1/employee";

        String GET_BY_ID = "/api/v1/employee/{id}";
    }
}
