package com.reliaquest.api.service.consumer.impl;

import static com.reliaquest.api.util.TestStaticEmployees.lukeDeleteInput;
import static com.reliaquest.api.util.TestStaticEmployees.lukeInput;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reliaquest.api.exception.InternalServerError;
import com.reliaquest.api.exception.TooManyRequestException;
import com.reliaquest.api.model.DeleteEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.util.Constants;
import com.reliaquest.api.util.TestStaticEmployees;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
class EmployeeClientTest {

    private ClientAndServer mockServer;

    private EmployeeClient employeeClient;

    public ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                throw new InternalServerError(clientResponse.statusCode().value());
            } else if (clientResponse.statusCode().value() == 429) {
                throw new TooManyRequestException();
            } else {
                return Mono.just(clientResponse);
            }
        });
    }

    @BeforeEach
    public void setupMockServer() {
        mockServer = ClientAndServer.startClientAndServer(8112);
        employeeClient = new EmployeeClient();
        ReflectionTestUtils.setField(
                employeeClient,
                "webClientBuilder",
                WebClient.builder()
                        .filter(TestStaticEmployees.errorHandler())
                        .baseUrl("http://localhost:" + mockServer.getLocalPort()));
    }

    @Test
    void whenCreateEmployeeApi_IfGivesSuccess_thenReturnListOfEmployees() throws JsonProcessingException {
        mockServer
                .when(request()
                        .withMethod(HttpMethod.POST.name())
                        .withPath(Constants.API_PATH_EMPLOYEE.BASE)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(TestStaticEmployees.createEmployeeApiRequest(lukeInput())))
                .respond(response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(TestStaticEmployees.createEmployeeResponseFromApi()));

        Employee createdEmployee = employeeClient.createEmployee(lukeInput());

        assertThat(createdEmployee).isNotNull();
        assertThat(createdEmployee.getEmployeeName()).isEqualTo("Luke Skywalker");

        mockServer.verify(request()
                .withMethod(HttpMethod.POST.name())
                .withPath(Constants.API_PATH_EMPLOYEE.BASE)
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(TestStaticEmployees.createEmployeeApiRequest(lukeInput())));
    }

    @Test
    void whenCreateEmployeeApi_IfGivesInternalServerErrorResponse_thenReturnInternalServerErrorResponse()
            throws JsonProcessingException {
        mockServer
                .when(request()
                        .withMethod(HttpMethod.POST.name())
                        .withPath(Constants.API_PATH_EMPLOYEE.BASE)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(TestStaticEmployees.createEmployeeApiRequest(lukeInput())))
                .respond(response().withStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        Assertions.assertThrows(InternalServerError.class, () -> employeeClient.createEmployee(lukeInput()));

        mockServer.verify(request()
                .withMethod(HttpMethod.POST.name())
                .withPath(Constants.API_PATH_EMPLOYEE.BASE)
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(TestStaticEmployees.createEmployeeApiRequest(lukeInput())));
    }

    @Test
    void whenCreateEmployeeApi_IfGivesTooManyRequestErrorResponse_thenReturnTooManyRequestErrorResponse()
            throws JsonProcessingException {
        mockServer
                .when(request()
                        .withMethod(HttpMethod.POST.name())
                        .withPath(Constants.API_PATH_EMPLOYEE.BASE)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(TestStaticEmployees.createEmployeeApiRequest(lukeInput())))
                .respond(response().withStatusCode(HttpStatus.TOO_MANY_REQUESTS.value()));

        Assertions.assertThrows(TooManyRequestException.class, () -> employeeClient.createEmployee(lukeInput()));

        mockServer.verify(request()
                .withMethod(HttpMethod.POST.name())
                .withPath(Constants.API_PATH_EMPLOYEE.BASE)
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(TestStaticEmployees.createEmployeeApiRequest(lukeInput())));
    }

    @Test
    void whenDeleteEmployeeByIdApi_IfGivesInternalServerErrorResponse_thenReturnInternalServerErrorResponse()
            throws JsonProcessingException {
        DeleteEmployeeInput input = lukeDeleteInput();
        mockServer
                .when(request()
                        .withMethod(HttpMethod.DELETE.name())
                        .withPath(Constants.API_PATH_EMPLOYEE.BASE)
                        .withBody(TestStaticEmployees.createEmployeeApiRequest(lukeDeleteInput())))
                .respond(response().withStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        Assertions.assertThrows(InternalServerError.class, () -> employeeClient.deleteEmployee(lukeDeleteInput()));

        mockServer.verify(request().withMethod(HttpMethod.DELETE.name()).withPath(Constants.API_PATH_EMPLOYEE.BASE));
    }

    @Test
    void whenDeleteEmployeeByIdApi_IfGivesTooManyRequestErrorResponse_thenReturnTooManyRequestErrorResponse() {
        mockServer
                .when(request().withMethod(HttpMethod.DELETE.name()).withPath(Constants.API_PATH_EMPLOYEE.BASE))
                .respond(response().withStatusCode(HttpStatus.TOO_MANY_REQUESTS.value()));

        Assertions.assertThrows(TooManyRequestException.class, () -> employeeClient.deleteEmployee(lukeDeleteInput()));

        mockServer.verify(request().withMethod(HttpMethod.DELETE.name()).withPath(Constants.API_PATH_EMPLOYEE.BASE));
    }

    @Test
    void whenGetAllEmployeesApi_IfGivesSuccess_thenReturnListOfEmployees() {
        String responseBody = TestStaticEmployees.getAllEmployeesResponseFromApi();

        mockServer
                .when(request().withMethod(HttpMethod.GET.name()).withPath(Constants.API_PATH_EMPLOYEE.BASE))
                .respond(response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(responseBody));

        final List<Employee> employees = employeeClient.getAllEmployees();

        assertThat(employees).hasSize(50);
        assertThat(employees.get(0).getId()).isEqualTo("cfb40208-a9ce-4137-8948-0b42f73c041a");
        assertThat(employees.get(0).getEmployeeName()).isEqualTo("Arleen Effertz");
        assertThat(employees.get(0).getEmployeeSalary()).isEqualTo(394222);
        assertThat(employees.get(0).getEmployeeAge()).isEqualTo(57);
        assertThat(employees.get(0).getEmployeeEmail()).isEqualTo("stim@company.com");
        assertThat(employees.get(0).getEmployeeTitle()).isEqualTo("International Design Administrator");

        mockServer.verify(request().withMethod(HttpMethod.GET.name()).withPath(Constants.API_PATH_EMPLOYEE.BASE));
    }

    @Test
    void whenGetAllEmployeesApi_IfGivesErrorResponse_thenReturnErrorResponse() {
        mockServer
                .when(request().withMethod(HttpMethod.GET.name()).withPath(Constants.API_PATH_EMPLOYEE.BASE))
                .respond(response().withStatusCode(HttpStatus.TOO_MANY_REQUESTS.value()));

        Assertions.assertThrows(TooManyRequestException.class, () -> employeeClient.getAllEmployees());
    }

    @Test
    void whenGetEmployeeByIdApi_IfGivesSuccess_thenReturnOptionalEmployee() {
        String employeeByIdResponse = TestStaticEmployees.getAllEmployeeByIdFromApi();

        mockServer
                .when(request()
                        .withMethod(HttpMethod.GET.name())
                        .withPath("/api/v1/employee/cfb40208-a9ce-4137-8948-0b42f73c041a"))
                .respond(response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(employeeByIdResponse));

        final Optional<Employee> optionalEmployee =
                employeeClient.getEmployeeById("cfb40208-a9ce-4137-8948-0b42f73c041a");

        assertThat(optionalEmployee).isNotNull();

        Employee employee = optionalEmployee.get();
        assertThat(employee.getId()).isEqualTo("cfb40208-a9ce-4137-8948-0b42f73c041a");
        assertThat(employee.getEmployeeName()).isEqualTo("Arleen Effertz");
        assertThat(employee.getEmployeeAge()).isEqualTo(57);
        assertThat(employee.getEmployeeSalary()).isEqualTo(394222);

        mockServer.verify(request()
                .withMethod(HttpMethod.GET.name())
                .withPath("/api/v1/employee/cfb40208-a9ce-4137-8948-0b42f73c041a"));
    }

    @Test
    void whenGetEmployeeByIdApi_IfGivesInternalServerErrorResponse_thenReturnInternalServerErrorResponse() {
        mockServer
                .when(request()
                        .withMethod(HttpMethod.GET.name())
                        .withPath("/api/v1/employee/cfb40208-a9ce-4137-8948-0b42f73c041a"))
                .respond(response().withStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        Assertions.assertThrows(
                InternalServerError.class,
                () -> employeeClient.getEmployeeById("cfb40208-a9ce-4137-8948-0b42f73c041a"));
    }

    @Test
    void whenGetEmployeeByIdApi_IfGivesTooManyRequestErrorResponse_thenReturnTooManyRequestErrorResponse() {
        mockServer
                .when(request()
                        .withMethod(HttpMethod.GET.name())
                        .withPath("/api/v1/employee/cfb40208-a9ce-4137-8948-0b42f73c041a"))
                .respond(response().withStatusCode(HttpStatus.TOO_MANY_REQUESTS.value()));

        Assertions.assertThrows(
                TooManyRequestException.class,
                () -> employeeClient.getEmployeeById("cfb40208-a9ce-4137-8948-0b42f73c041a"));
    }

    @AfterEach
    public void tearDownServer() {
        mockServer.stop();
    }
}
