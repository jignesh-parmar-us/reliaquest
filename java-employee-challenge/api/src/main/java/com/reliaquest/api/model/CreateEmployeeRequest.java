package com.reliaquest.api.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record CreateEmployeeRequest(
        @NotBlank(message = "Name cannot be blank") String name,
        @Positive(message = "Salary should be positive") @NotNull(message = "Salary cannot be null") Integer salary,
        @Min(16) @Max(75) Integer age,
        @NotBlank String title,
        @NotNull(message = "Email cannot be null") @Email String email) {

    public static String toJson(CreateEmployeeRequest employee) {
        return """
				    {
				    "name": "%s",
				    "salary": %d,
				    "age": %d,
				    "title": "%s",
				    "email": "%s"
				}
				"""
                .formatted(employee.name(), employee.salary(), employee.age(), employee.title(), employee.email());
    }

    public static EmployeeEntity to(CreateEmployeeRequest createEmployeeRequest) {
        return new EmployeeEntity(
                UUID.randomUUID().toString(),
                createEmployeeRequest.name(),
                createEmployeeRequest.salary(),
                createEmployeeRequest.age(),
                createEmployeeRequest.title(),
                createEmployeeRequest.email());
    }
}
