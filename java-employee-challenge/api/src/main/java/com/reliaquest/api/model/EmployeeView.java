package com.reliaquest.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(EmployeeView.PrefixNamingStrategy.class)
public record EmployeeView(String id, String name, Integer salary, Integer age, String title, String email) {

    public static EmployeeView from(EmployeeEntity employeeEntity) {
        return new EmployeeView(
                employeeEntity.id(),
                employeeEntity.name(),
                employeeEntity.salary(),
                employeeEntity.age(),
                employeeEntity.title(),
                employeeEntity.email());
    }

    public static EmployeeView from(EmployeeView e, Integer salary, String name) {
        return new EmployeeView(e.id(), name, salary, e.age(), e.title(), e.email());
    }

    public static String toJson(EmployeeView employee) {
        return """
                {
                "id": "%s",
                "employee_name": "%s",
                "employee_salary": %d,
                "employee_age": %d,
                "employee_title": "%s",
                "employee_email": "%s"
            }
            """
                .formatted(
                        employee.id(),
                        employee.name(),
                        employee.salary(),
                        employee.age(),
                        employee.title(),
                        employee.email());
    }

    static class PrefixNamingStrategy extends PropertyNamingStrategies.NamingBase {

        private static final long serialVersionUID = 1L;

        @Override
        public String translate(String propertyName) {
            if ("id".equals(propertyName)) {
                return propertyName;
            }
            return "employee_" + propertyName;
        }
    }
}
