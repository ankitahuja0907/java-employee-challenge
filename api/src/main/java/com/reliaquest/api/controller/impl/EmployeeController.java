package com.reliaquest.api.controller.impl;

import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.provider.IEmployeeService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("employee")
@Slf4j
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeInput> {

    @Autowired
    private IEmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        log.info("getEmployeesByNameSearch : searchString {} ", searchString);
        List<Employee> filteredEmployees = employeeService.getEmployeesByNameSearch(searchString);
        log.info("getEmployeesByNameSearch :  filteredEmployees {}", filteredEmployees);
        return ResponseEntity.ok(filteredEmployees);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        log.info("getEmployeeById : Employee Id {}", id);
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        final Integer highestSalaryOfEmployees = employeeService.getHighestSalaryOfEmployees();
        log.info("getHighestSalaryOfEmployees result :{}", highestSalaryOfEmployees);
        return ResponseEntity.ok(highestSalaryOfEmployees);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        final List<String> topTenHighestEarningEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();
        log.info("GetTopTenHighestEarningEmployeeNames : List :{}", topTenHighestEarningEmployeeNames);
        return ResponseEntity.ok(topTenHighestEarningEmployeeNames);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(CreateEmployeeInput employeeInput) {
        Employee employee = employeeService.createEmployee(employeeInput);
        log.info("CreateEmployee : Leaving Controller");
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {

        final String nameOfDeletedEmployee = employeeService.deleteEmployee(id);
        log.info("DeleteEmployeeById : deleted Employee :{}", nameOfDeletedEmployee);
        return ResponseEntity.ok(nameOfDeletedEmployee);
    }
}
