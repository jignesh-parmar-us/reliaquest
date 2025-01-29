package com.reliaquest.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
public class EmployeeResponseModelJsonTest {

    @Autowired
    private JacksonTester<EmployeeView> jacksonTester;

    EmployeeView employee;
    String employeeResponseModelJson;

    @BeforeEach
    void setup() {
        employee = new EmployeeView(
                "28a71512-247a-45e7-a135-47cf9663073f",
                "Briana Schiller",
                222890,
                58,
                "Mining Officer",
                "fixsan@company.com");
        employeeResponseModelJson = EmployeeView.toJson(employee);
    }

    @Test
    void shouldSerializePost() throws Exception {
        assertThat(jacksonTester.write(employee)).isEqualToJson(employeeResponseModelJson);
    }

    @Test
    void shouldDeserializePost() throws Exception {
        assertThat(jacksonTester.parse(employeeResponseModelJson)).isEqualTo(employee);
    }
}
