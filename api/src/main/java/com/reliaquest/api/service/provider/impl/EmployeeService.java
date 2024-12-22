package com.reliaquest.api.service.provider.impl;

import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.DeleteEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.Top10Employees;
import com.reliaquest.api.service.consumer.impl.EmployeeClient;
import com.reliaquest.api.service.provider.IEmployeeService;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmployeeService implements IEmployeeService {

    @Autowired
    EmployeeClient employeeClient;

    @Autowired
    Top10Employees top10Employees;

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> employees = employeeClient.getAllEmployees();
        if (top10Employees.getTop10Employees().isEmpty()) {
            top10Employees.addEmployees(employees);
        }
        log.info("getAllEmployees - No of Employees {}", employees.size());
        return employees;
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        final List<Employee> allEmployees = getAllEmployees();
        final List<Employee> filteredEmployees = allEmployees.parallelStream()
                .filter(employee -> StringUtils.containsIgnoreCase(employee.getEmployeeName(), searchString))
                .toList();

        log.info("getEmployeesByNameSearch: Search Results {}", filteredEmployees);

        return filteredEmployees;
    }

    @Override
    public Employee getEmployeeById(String id) {
        log.info("getEmployeeById- Get employee by Id {}", id);

        Optional<Employee> optionalEmployee = employeeClient.getEmployeeById(id);

        if (optionalEmployee.isEmpty()) {
            log.error("getEmployeeById- Employee Not Found with id {}", id);
            throw new EmployeeNotFoundException(id);
        }

        log.info("getEmployeeById- employee found : {}", optionalEmployee.get());
        return optionalEmployee.get();
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        Employee highestSalaryEmployee = top10Employees.getHighestSalaryEmployee();
        if (highestSalaryEmployee != null) {
            return highestSalaryEmployee.getEmployeeSalary();
        }
        log.info("top10 list is not ready, fetching all employees");
        getAllEmployees();
        log.info("Refilled top10 Employee List");
        highestSalaryEmployee = top10Employees.getHighestSalaryEmployee();
        if (highestSalaryEmployee != null) {
            return highestSalaryEmployee.getEmployeeSalary();
        }
        log.info("Found no employees after refilling top10 list");
        return null;
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        List<Employee> top10EmployeeList = top10Employees.getTop10Employees();
        if (top10EmployeeList.size() == 10) {
            return top10EmployeeList.stream().map(Employee::getEmployeeName).toList();
        }
        log.info("top10 list is not ready, fetching all employees");
        getAllEmployees();
        log.info("top10 List is filled. Returning the list");
        top10EmployeeList = top10Employees.getTop10Employees();
        return top10EmployeeList.stream().map(Employee::getEmployeeName).toList();
    }

    @Override
    public Employee createEmployee(CreateEmployeeInput employeeInput) {
        Employee employee = employeeClient.createEmployee(employeeInput);
        log.info("createEmployee- Employee Created {}", employee);
        top10Employees.addEmployee(employee);
        return employee;
    }

    @Override
    public String deleteEmployee(String id) {
        final Employee employee = getEmployeeById(id);
        DeleteEmployeeInput input =
                DeleteEmployeeInput.builder().name(employee.getEmployeeName()).build();
        String status = employeeClient.deleteEmployee(input);
        log.info("DeleteEmployee: Employee deletion status {}", status);
        top10Employees.deleteEmployee(employee);
        final String employeeName = employee.getEmployeeName();
        log.info("DeleteEmployee: Employee Name {}", employeeName);
        return employeeName;
    }
}
