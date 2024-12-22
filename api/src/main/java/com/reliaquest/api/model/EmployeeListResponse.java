package com.reliaquest.api.model;

import java.util.List;
import lombok.Data;

@Data
public class EmployeeListResponse {
    List<Employee> data;
    String status;
    String message;
}
