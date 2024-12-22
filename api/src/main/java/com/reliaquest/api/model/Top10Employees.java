package com.reliaquest.api.model;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import org.springframework.stereotype.Component;

@Component
public class Top10Employees {
    private final TreeSet<Employee> employeeSet;

    public Top10Employees() {
        this.employeeSet = new TreeSet<>(
                Comparator.comparingInt(Employee::getEmployeeSalary).reversed());
    }

    public void addEmployee(Employee employee) {
        employeeSet.add(employee);
        // Remove the smallest employee if the size exceeds 10
        if (employeeSet.size() > 10) {
            employeeSet.pollLast();
        }
    }

    // Method to add an entire list of employees to the TreeSet
    public void addEmployees(List<Employee> employees) {
        employeeSet.addAll(employees);
        if (employeeSet.size() > 10) {
            // Remove the smallest employee if the size exceeds 10
            while (employeeSet.size() > 10) {
                employeeSet.pollLast();
            }
        }
    }

    public void deleteEmployee(Employee employee) {
        employeeSet.remove(employee);
    }

    public List<Employee> getTop10Employees() {
        return employeeSet.stream().limit(10).toList();
    }

    public Employee getHighestSalaryEmployee() {
        if (!employeeSet.isEmpty()) {
            return employeeSet.first();
        }
        return null;
    }
}
