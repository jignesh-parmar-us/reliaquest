package com.reliaquest.api.model;

import jakarta.validation.constraints.NotBlank;

public record DeleteEmployeeRequest(@NotBlank String name) {}
