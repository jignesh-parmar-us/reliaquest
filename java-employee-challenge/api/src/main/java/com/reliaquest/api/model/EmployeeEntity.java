package com.reliaquest.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(EmployeeEntity.PrefixNamingStrategy.class)
public record EmployeeEntity(String id, String name, Integer salary, Integer age, String title, String email) {

    public static EmployeeEntity from(EmployeeEntity e, Integer salary, String name) {
        return new EmployeeEntity(e.id(), name, salary, e.age(), e.title(), e.email());
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
