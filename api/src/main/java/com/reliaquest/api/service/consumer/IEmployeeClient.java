package com.reliaquest.api.service.consumer;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.DeleteEmployeeInput;
import com.reliaquest.api.model.Employee;
import java.util.List;
import java.util.Optional;

public interface IEmployeeClient {

    public Employee createEmployee(CreateEmployeeInput input);

    public Optional<Employee> getEmployeeById(String id);

    public List<Employee> getAllEmployees();

    public String deleteEmployee(DeleteEmployeeInput input);
}
