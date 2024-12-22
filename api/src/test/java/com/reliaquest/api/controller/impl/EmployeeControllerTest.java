package com.reliaquest.api.controller.impl;

import static com.reliaquest.api.util.TestStaticEmployees.arleen;
import static com.reliaquest.api.util.TestStaticEmployees.getAllEmployees;
import static com.reliaquest.api.util.TestStaticEmployees.luke;
import static com.reliaquest.api.util.TestStaticEmployees.lukeInput;
import static com.reliaquest.api.util.TestStaticEmployees.shirleen;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.exception.ApiResponseJsonParseException;
import com.reliaquest.api.exception.BadRequestException;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.InternalServerError;
import com.reliaquest.api.exception.TooManyRequestException;
import com.reliaquest.api.exception.handler.CustomExceptionHandler;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.provider.IEmployeeService;
import com.reliaquest.api.util.TestConstants;
import com.reliaquest.api.util.TestStaticEmployees;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = IEmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private IEmployeeService employeeService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(IEmployeeController.class)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    void whenGetAllEmployees_thenReturnJsonArray() throws Exception {

        List<Employee> allEmployees = getAllEmployees();

        given(employeeService.getAllEmployees()).willReturn(allEmployees);

        mockMvc.perform(get("/employee").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(50)));
    }

    @Test
    void whenGetAllEmployees_throwsTooManyRequestException() throws Exception {
        given(employeeService.getAllEmployees()).willThrow(new TooManyRequestException());

        mockMvc.perform(get("/employee").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.status", is(429)));
    }

    @Test
    void whenGetAllEmployees_throwsApiResponseJsonParseException() throws Exception {
        given(employeeService.getAllEmployees()).willThrow(new ApiResponseJsonParseException());

        mockMvc.perform(get("/employee").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.status", is(500)));
    }

    @Test
    void whenGetEmployeeById_thenReturnEmployee() throws Exception {
        Employee dhairya = luke();

        given(employeeService.getEmployeeById("1")).willReturn(dhairya);

        mockMvc.perform(get("/employee/{id}", 1).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name", is(dhairya.getEmployeeName())))
                .andExpect(jsonPath("$.employee_salary", is(dhairya.getEmployeeSalary())))
                .andExpect(jsonPath("$.employee_age", is(dhairya.getEmployeeAge())));
    }

    @Test
    void whenGetEmployeeById_throwsTooManyRequestException() throws Exception {
        given(employeeService.getEmployeeById("1")).willThrow(new TooManyRequestException());

        mockMvc.perform(get("/employee/{id}", "1").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.status", is(429)));
    }

    @Test
    void whenGetEmployeeById_throwsInternalServerError() throws Exception {
        given(employeeService.getEmployeeById("1")).willThrow(new InternalServerError(500));

        mockMvc.perform(get("/employee/{id}", "1").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.status", is(500)));
    }

    @Test
    void whenGetEmployeeById_throwsApiResponseJsonParseException() throws Exception {
        given(employeeService.getEmployeeById("1")).willThrow(new ApiResponseJsonParseException());

        mockMvc.perform(get("/employee/{id}", "1").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.status", is(500)));
    }

    @Test
    void whenGetEmployeeById_IfNotPresent_throwsEmployeeNotFoundException() throws Exception {
        String uuid = UUID.randomUUID().toString();
        given(employeeService.getEmployeeById(uuid)).willThrow(new EmployeeNotFoundException(uuid));

        mockMvc.perform(get("/employee/{id}", uuid).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Employee Not found with id " + uuid)));
    }

    @Test
    void whenGetEmployeeByNameSearch_thenReturnEmployeeList() throws Exception {

        String searchString = "leen";
        given(employeeService.getEmployeesByNameSearch(searchString)).willReturn(List.of(arleen(), shirleen()));
        mockMvc.perform(get("/employee/search/{searchString}", searchString).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].employee_name", is(arleen().getEmployeeName())));
    }

    @Test
    void whenGetEmployeeByNameSearch_IfNotPresent_thenReturnEmptyEmployeeList() throws Exception {

        String searchString = "Ca";

        given(employeeService.getEmployeesByNameSearch(searchString)).willReturn(new ArrayList<>());

        mockMvc.perform(get("/employee/search/{searchString}", searchString).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void whenGetEmployeeByNameSearch_IfTooManyRequests_throwsTooManyRequestException() throws Exception {
        String searchString = "Ca";

        given(employeeService.getEmployeesByNameSearch(searchString)).willThrow(new TooManyRequestException());

        mockMvc.perform(get("/employee/search/{searchString}", searchString).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.status", is(429)));
    }

    @Test
    void whenGetEmployeeByNameSearch_IfApiResponseParseFails_throwsApiResponseJsonParseException() throws Exception {
        String searchString = "Ca";

        given(employeeService.getEmployeesByNameSearch(searchString)).willThrow(new ApiResponseJsonParseException());

        mockMvc.perform(get("/employee/search/{searchString}", searchString).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.status", is(500)));
    }

    @Test
    void whenGetHighestSalaryOfEmployee_thenReturnHighestSalary() throws Exception {

        given(employeeService.getHighestSalaryOfEmployees()).willReturn(493762);

        mockMvc.perform(get("/employee/highestSalary"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("493762"));
    }

    @Test
    void whenGetHighestSalaryOfEmployee_IfTooManyRequest_throwsTooManyRequestsException() throws Exception {

        given(employeeService.getHighestSalaryOfEmployees()).willThrow(new TooManyRequestException());

        mockMvc.perform(get("/employee/highestSalary"))
                .andDo(print())
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.status", is(429)));
    }

    @Test
    void whenGetTopTenHighestEarningEmployeeNames_thenReturnListOfEmployeeName() throws Exception {

        given(employeeService.getTopTenHighestEarningEmployeeNames())
                .willReturn(TestStaticEmployees.topTenEmployeeNames());

        mockMvc.perform(get("/employee/topTenHighestEarningEmployeeNames").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$.[0]", is("Mrs. Thomas Volkman")))
                .andExpect(jsonPath("$.[9]", is("Arleen Effertz")));
    }

    @Test
    void whenGetTopTenHighestEarningEmployeeNames_IfTooManyRequests_throwTooManyRequestsException() throws Exception {

        given(employeeService.getTopTenHighestEarningEmployeeNames()).willThrow(new TooManyRequestException());

        mockMvc.perform(get("/employee/topTenHighestEarningEmployeeNames"))
                .andDo(print())
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.status", is(429)));
    }

    @Test
    void whenCreateEmployee_thenReturnCreatedEmployee() throws Exception {
        CreateEmployeeInput input = lukeInput();
        Employee employee = luke();

        given(employeeService.createEmployee(input)).willReturn(employee);

        mockMvc.perform(post("/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name", is(employee.getEmployeeName())))
                .andExpect(jsonPath("$.employee_age", is(employee.getEmployeeAge())))
                .andExpect(jsonPath("$.employee_salary", is(employee.getEmployeeSalary())))
                .andExpect(jsonPath("$.employee_title", is(employee.getEmployeeTitle())))
                .andExpect(jsonPath("$.employee_email", is(employee.getEmployeeEmail())));
    }

    @Test
    void whenCreateEmployee_IfInputIsInvalid_thenThrowBadRequestException() throws Exception {
        CreateEmployeeInput input = lukeInput();

        given(employeeService.createEmployee(input))
                .willThrow(new BadRequestException(TestConstants.BAD_INPUT_EXCEPTION_MESSAGE));

        mockMvc.perform(post("/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is(TestConstants.BAD_INPUT_EXCEPTION_MESSAGE)));
    }

    @Test
    void whenDeleteEmployee_thenReturnSuccess() throws Exception {
        String id = "2";
        given(employeeService.deleteEmployee(id)).willReturn(TestConstants.SUCCESS);

        mockMvc.perform(delete("/employee/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(TestConstants.SUCCESS));
    }

    @Test
    void whenDeleteEmployee_IfTooManyRequests_thenThrowsTooManyRequests() throws Exception {
        String id = "2";
        given(employeeService.deleteEmployee(id)).willThrow(new TooManyRequestException());

        mockMvc.perform(delete("/employee/{id}", id))
                .andDo(print())
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.status", is(429)));
    }

    @Test
    void whenDeleteEmployee_IfServerReturnError_thenThrowsInternalServerError() throws Exception {
        String id = "2";
        given(employeeService.deleteEmployee(id)).willThrow(new InternalServerError(500));

        mockMvc.perform(delete("/employee/{id}", id))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.status", is(500)));
    }

    @Test
    void whenDeleteEmployee_IfResponseJsonParsingFails_thenThrowsApiResponseJsonParseException() throws Exception {
        String id = "2";
        given(employeeService.deleteEmployee(id)).willThrow(new ApiResponseJsonParseException());

        mockMvc.perform(delete("/employee/{id}", id))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.status", is(500)));
    }
}
