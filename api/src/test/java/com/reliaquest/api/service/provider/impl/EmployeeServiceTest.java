package com.reliaquest.api.service.provider.impl;

import static com.reliaquest.api.util.TestStaticEmployees.arleen;
import static com.reliaquest.api.util.TestStaticEmployees.getAllEmployees;
import static com.reliaquest.api.util.TestStaticEmployees.luke;
import static com.reliaquest.api.util.TestStaticEmployees.lukeDeleteInput;
import static com.reliaquest.api.util.TestStaticEmployees.lukeInput;
import static com.reliaquest.api.util.TestStaticEmployees.shirleen;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.reliaquest.api.exception.ApiResponseJsonParseException;
import com.reliaquest.api.exception.BadRequestException;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.InternalServerError;
import com.reliaquest.api.exception.TooManyRequestException;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.Top10Employees;
import com.reliaquest.api.service.consumer.impl.EmployeeClient;
import com.reliaquest.api.util.TestConstants;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @MockBean
    private EmployeeClient employeeClient;

    @MockBean
    private Top10Employees top10Employees;

    @Test
    void givenEmployeeList_whenGetAllEmployees_thenListOfEmployeeShouldBeReturned() {

        List<Employee> allEmployees = getAllEmployees();

        given(employeeClient.getAllEmployees()).willReturn(allEmployees);

        List<Employee> employees = employeeService.getAllEmployees();

        assertThat(employees).isNotNull();
        assertThat(employees).hasSize(50);
        assertThat(employees).containsAll(allEmployees);
        assertThat(employees).extracting("id").contains(arleen().getId());
        assertThat(employees).extracting("employeeName").contains(arleen().getEmployeeName());
    }

    @Test
    void givenEmptyEmployeeList_whenGetAllEmployees_thenReturnEmptyEmployeeList() {

        given(employeeClient.getAllEmployees()).willReturn(Collections.emptyList());

        List<Employee> employees = employeeService.getAllEmployees();

        assertThat(employees).isEmpty();
        assertThat(employees).hasSize(0);
    }

    @Test
    void whenGetAllEmployees_thenThrowsTooManyRequests() {
        given(employeeClient.getAllEmployees()).willThrow(new TooManyRequestException());
        assertThrows(TooManyRequestException.class, () -> employeeService.getAllEmployees());
    }

    @Test
    void whenGetAllEmployees_thenThrowsApiResponseJsonParseException() {
        given(employeeClient.getAllEmployees()).willThrow(new ApiResponseJsonParseException());
        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.getAllEmployees());
    }

    @Test
    void whenGetEmployeeById_thenReturnEmployee() {
        String empId = UUID.randomUUID().toString();
        final Employee luke = luke();
        given(employeeClient.getEmployeeById(empId)).willReturn(Optional.of(luke));

        Employee employeeById = employeeService.getEmployeeById(empId);
        assertThat(employeeById).isNotNull();
        assertThat(employeeById).isEqualTo(luke);

        assertThat(employeeById).extracting("id").isEqualTo(luke.getId());
        assertThat(employeeById).extracting("employeeName").isEqualTo(luke.getEmployeeName());
        assertThat(employeeById).extracting("employeeAge").isEqualTo(luke.getEmployeeAge());
        assertThat(employeeById).extracting("employeeSalary").isEqualTo(luke.getEmployeeSalary());
    }

    @Test
    void whenGetEmployeeById_thenThrowsEmployeeNotFoundException() {
        String empId = UUID.randomUUID().toString();
        given(employeeClient.getEmployeeById(empId)).willThrow(new EmployeeNotFoundException(empId));

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(empId));
    }

    @Test
    void whenGetEmployeeById_thenThrowsTooManyRequestsException() {
        String empId = UUID.randomUUID().toString();
        given(employeeClient.getEmployeeById(empId)).willThrow(new TooManyRequestException());

        assertThrows(TooManyRequestException.class, () -> employeeService.getEmployeeById(empId));
    }

    @Test
    void whenGetEmployeeById_thenThrowsApiResponseJsonParseException() {
        String empId = UUID.randomUUID().toString();
        given(employeeClient.getEmployeeById(empId)).willThrow(new ApiResponseJsonParseException());

        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.getEmployeeById(empId));
    }

    @Test
    void whenGetEmployeesByNameSearch_returnMatchingEmployeeList() {

        Employee arleen = arleen();
        Employee shirleen = shirleen();

        given(employeeClient.getAllEmployees()).willReturn(getAllEmployees());

        List<Employee> filteredEmployees = employeeService.getEmployeesByNameSearch("leen");

        assertThat(filteredEmployees).hasSize(2);
        assertThat(filteredEmployees).extracting("id").contains(arleen.getId(), shirleen.getId());
        assertThat(filteredEmployees)
                .extracting("employeeName")
                .contains(arleen.getEmployeeName(), shirleen.getEmployeeName());
    }

    @Test
    void whenGetEmployeesByNameSearch_IfTooManyRequests_throwsTooManyRequestException() {
        given(employeeClient.getAllEmployees()).willThrow(new TooManyRequestException());

        final String searchString = "leen";

        assertThrows(TooManyRequestException.class, () -> employeeService.getEmployeesByNameSearch(searchString));
    }

    @Test
    void whenGetEmployeesByNameSearch_IfApiResponseParsingFails_throwsApiResponseJsonParseException() {
        given(employeeClient.getAllEmployees()).willThrow(new ApiResponseJsonParseException());
        final String searchString = "leen";
        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.getEmployeesByNameSearch(searchString));
    }

    @Test
    void whenGetHighestSalaryOfEmployee_WhenTop10Exists() {
        Employee highestSalaryEmployee = luke();
        given(top10Employees.getHighestSalaryEmployee()).willReturn(highestSalaryEmployee);
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        assertThat(highestSalary).isEqualTo(300000);
        verify(top10Employees, times(1)).getHighestSalaryEmployee();
    }

    @Test
    void whenGetHighestSalaryOfEmployee_WhenTop10DoesNotExist() {
        given(top10Employees.getHighestSalaryEmployee()).willReturn(null);
        given(employeeClient.getAllEmployees()).willReturn(getAllEmployees());
        employeeService.getHighestSalaryOfEmployees();
        verify(top10Employees, times(2)).getHighestSalaryEmployee();
    }

    @Test
    void whenGetHighestSalaryOfEmployee_IfTooManyRequests_thenThrowsTooManyRequestException() {
        given(top10Employees.getHighestSalaryEmployee()).willReturn(null);
        given(employeeClient.getAllEmployees()).willThrow(new TooManyRequestException());
        assertThrows(TooManyRequestException.class, () -> employeeService.getHighestSalaryOfEmployees());
    }

    @Test
    void whenGetHighestSalaryOfEmployee_IfApiResponseParsingFails_thenThrowsApiResponseJsonParseException() {
        given(top10Employees.getHighestSalaryEmployee()).willReturn(null);
        given(employeeClient.getAllEmployees()).willThrow(new ApiResponseJsonParseException());
        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.getHighestSalaryOfEmployees());
    }

    @Test
    void whenGetTopTenHighestEarningEmployeeNames_IfTooManyRequests_thenThrowsTooManyRequestException() {
        given(employeeClient.getAllEmployees()).willThrow(new TooManyRequestException());
        assertThrows(TooManyRequestException.class, () -> employeeService.getTopTenHighestEarningEmployeeNames());
    }

    @Test
    void whenGetTopTenHighestEarningEmployeeNames_IfApiResponseParsingFails_thenThrowsApiResponseJsonParseException() {
        given(employeeClient.getAllEmployees()).willThrow(new ApiResponseJsonParseException());
        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.getTopTenHighestEarningEmployeeNames());
    }

    @Test
    void whenCreateEmployee_IfSuccess_thenReturnCreatedEmployee() {
        CreateEmployeeInput input = lukeInput();
        Employee luke = luke();
        given(employeeClient.createEmployee(input)).willReturn(luke);
        Employee createdEmployee = employeeService.createEmployee(input);
        assertThat(createdEmployee).isEqualTo(luke);
    }

    @Test
    void whenCreateEmployee_IfInputIsInvalid_thenThrowBadRequestException() {
        CreateEmployeeInput input = lukeInput();
        lenient()
                .when(employeeClient.createEmployee(input))
                .thenThrow(new BadRequestException(TestConstants.BAD_INPUT_EXCEPTION_MESSAGE));

        assertThrows(BadRequestException.class, () -> employeeService.createEmployee(input));
    }

    @Test
    void whenCreateEmployee_IfTooManyRequests_thenThrowsTooManyRequestException() {
        CreateEmployeeInput input = lukeInput();

        when(employeeClient.createEmployee(input)).thenThrow(new TooManyRequestException());

        assertThrows(TooManyRequestException.class, () -> employeeService.createEmployee(input));
    }

    @Test
    void whenCreateEmployee_IfApiResponseParsingFails_thenThrowsApiResponseJsonParseException() {
        CreateEmployeeInput input = lukeInput();

        given(employeeClient.createEmployee(input)).willThrow(new ApiResponseJsonParseException());

        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.createEmployee(input));
    }

    @Test
    void whenDeleteEmployee_IfSuccess_thenGetDeletedEmployeeName() {
        Employee luke = luke();
        Optional<Employee> optionalEmployee = Optional.of(luke);
        final String empId = luke.getId().toString();
        given(employeeClient.getEmployeeById(empId)).willReturn(optionalEmployee);
        given(employeeClient.deleteEmployee(lukeDeleteInput())).willReturn(luke.getEmployeeName());

        assertThat(employeeService.deleteEmployee(empId)).isEqualTo(luke.getEmployeeName());
    }

    @Test
    void whenDeleteEmployee_IfServerReturnError_thenThrowsInternalServerError() {
        Employee luke = luke();
        Optional<Employee> optionalEmployee = Optional.of(luke);
        final String empId = luke.getId().toString();
        given(employeeClient.getEmployeeById(empId)).willReturn(optionalEmployee);
        given(employeeClient.deleteEmployee(lukeDeleteInput())).willThrow(new InternalServerError(500));

        assertThrows(InternalServerError.class, () -> employeeService.deleteEmployee(empId));
    }

    @Test
    void whenDeleteEmployee_IfTooManyRequests_thenThrowsTooManyRequestException() {
        Employee virat = luke();
        Optional<Employee> optionalEmployee = Optional.of(virat);
        final String empId = virat.getId().toString();
        given(employeeClient.getEmployeeById(empId)).willReturn(optionalEmployee);
        given(employeeClient.deleteEmployee(lukeDeleteInput())).willThrow(new TooManyRequestException());

        assertThrows(TooManyRequestException.class, () -> employeeService.deleteEmployee(empId));
    }
}
