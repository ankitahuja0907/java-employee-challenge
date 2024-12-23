package com.reliaquest.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateEmployeeInput {
    @NotBlank
    private String name;

    @Positive @NotNull private Integer salary;

    @Positive @NotNull private Integer age;

    @NotBlank
    private String title;
}
