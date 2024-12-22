package com.reliaquest.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.exception.InternalServerError;
import com.reliaquest.api.exception.TooManyRequestException;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.DeleteEmployeeInput;
import com.reliaquest.api.model.Employee;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Slf4j
public class TestStaticEmployees {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Employee luke() {
        return Employee.builder()
                .employeeName("Luke Skywalker")
                .employeeAge(19)
                .employeeSalary(300000)
                .employeeEmail("luke.skywalker@rebellion.org")
                .employeeTitle("Jedi Master")
                .id(UUID.randomUUID().toString())
                .build();
    }

    public static CreateEmployeeInput lukeInput() {
        return CreateEmployeeInput.builder()
                .name("Luke Skywalker")
                .age(19)
                .salary(300000)
                .title("Jedi Master")
                .build();
    }

    public static DeleteEmployeeInput lukeDeleteInput() {
        return DeleteEmployeeInput.builder().name("Luke Skywalker").build();
    }

    public static Employee arleen() {
        return Employee.builder()
                .employeeName("Arleen Effertz")
                .employeeAge(57)
                .employeeSalary(394222)
                .employeeEmail("stim@company.com")
                .employeeTitle("International Design Administrator")
                .id("cfb40208-a9ce-4137-8948-0b42f73c041a")
                .build();
    }

    public static Employee shirleen() {
        return Employee.builder()
                .employeeName("Ms. Shirleen Howe")
                .employeeAge(29)
                .employeeSalary(182908)
                .id("8b7993cd-534f-44c9-9c64-6cd6a52f8f2c")
                .employeeTitle("Real-Estate Producer")
                .employeeEmail("andalax@company.com")
                .build();
    }

    public static List<String> topTenEmployeeNames() {

        return List.of(
                "Mrs. Thomas Volkman",
                "Zackary Hauck MD",
                "Krysta Treutel",
                "Vella Willms",
                "Edythe Leuschke",
                "Miss Modesto Cummings",
                "Margarita Hettinger",
                "Felicidad Jenkins",
                "Carolee D'Amore",
                "Arleen Effertz");
    }

    public static List<Employee> getAllEmployees() {
        final String fileContents = readFile("data/allEmployees.json");
        List<Employee> employees = null;
        try {
            employees = objectMapper.readerForListOf(Employee.class).readValue(fileContents);
        } catch (JsonProcessingException e) {
            log.error("Error parsing File:{}", e.getMessage());
        }
        return employees;
    }

    private static String readFile(String fileName) {

        String jsonString = "";
        try {
            File file = new ClassPathResource(fileName).getFile();
            jsonString = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            log.error("Exception reading file :{}", ioException.getMessage());
        }
        return jsonString;
    }

    public static String getAllEmployeesResponseFromApi() {
        return readFile("data/response/getAllEmployeesResponse.json");
    }

    public static String getAllEmployeeByIdFromApi() {
        return readFile("data/response/getEmployeeByIdApiResponse.json");
    }

    public static String createEmployeeResponseFromApi() {
        return readFile("data/response/createEmployeeApiResponse.json");
    }

    public static String createEmployeeApiRequest(Object input) throws JsonProcessingException {
        return objectMapper.writeValueAsString(input);
    }

    public static ExchangeFilterFunction errorHandler() {
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
}
