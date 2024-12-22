package com.reliaquest.api.service.consumer.impl;

import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.DeleteEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeDeleteResponse;
import com.reliaquest.api.model.EmployeeListResponse;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.service.consumer.IEmployeeClient;
import com.reliaquest.api.util.Constants;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class EmployeeClient implements IEmployeeClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public Employee createEmployee(CreateEmployeeInput input) {
        return webClientBuilder
                .build()
                .post()
                .uri(uriBuilder ->
                        uriBuilder.path(Constants.API_PATH_EMPLOYEE.BASE).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(input)
                .retrieve()
                .bodyToMono(EmployeeResponse.class)
                .map(EmployeeResponse::getData)
                .block();
    }

    @Override
    public Optional<Employee> getEmployeeById(String id) {
        EmployeeResponse employeeResponse = webClientBuilder
                .build()
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(Constants.API_PATH_EMPLOYEE.GET_BY_ID).build(id))
                .retrieve()
                .onStatus(
                        HttpStatus.NOT_FOUND::equals, // Check if status is 404
                        response -> Mono.error(new EmployeeNotFoundException(id)) // Custom error handling
                        )
                .bodyToMono(EmployeeResponse.class)
                .block();
        return Optional.ofNullable(employeeResponse).map(EmployeeResponse::getData);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return webClientBuilder
                .build()
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(Constants.API_PATH_EMPLOYEE.BASE).build())
                .retrieve()
                .bodyToMono(EmployeeListResponse.class)
                .map(EmployeeListResponse::getData)
                .block();
    }

    @Override
    public String deleteEmployee(DeleteEmployeeInput input) {
        return webClientBuilder
                .build()
                .method(HttpMethod.DELETE)
                .uri(uriBuilder ->
                        uriBuilder.path(Constants.API_PATH_EMPLOYEE.BASE).build())
                .bodyValue(input)
                .retrieve()
                .bodyToMono(EmployeeDeleteResponse.class)
                .map(EmployeeDeleteResponse::getStatus)
                .block();
    }
}
