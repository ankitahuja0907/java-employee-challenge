package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Employee implements Serializable {
    private String id;

    @JsonProperty(value = "employee_name")
    private String employeeName;

    @JsonProperty(value = "employee_salary")
    private Integer employeeSalary;

    @JsonProperty(value = "employee_age")
    private Integer employeeAge;

    @JsonProperty(value = "employee_title")
    private String employeeTitle;

    @JsonProperty(value = "employee_email")
    private String employeeEmail;
}
