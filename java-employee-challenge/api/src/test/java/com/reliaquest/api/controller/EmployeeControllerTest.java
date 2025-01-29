package com.reliaquest.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceException;
import com.reliaquest.api.exception.MultipleEmployeeConflictInDeleteOperationException;
import com.reliaquest.api.exception.NoContentFoundException;
import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.EmployeeView;
import com.reliaquest.api.service.EmployeeService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EmployeeService employeeService;

    List<EmployeeView> employees = new ArrayList<>();

    @BeforeEach
    void setup() {
        employees = List.of(
                new EmployeeView(
                        "28a71512-247a-45e7-a135-47cf9663073f",
                        "Briana Schiller",
                        222890,
                        58,
                        "Mining Officer",
                        "fixsan@company.com"),
                new EmployeeView(
                        "3f21802a-446e-4383-a414-666e295af10a",
                        "Mrs. Chung Howell",
                        225208,
                        26,
                        "Principal Design Producer",
                        "zontrax@company.com"));
    }

    @Test
    void shouldReturnAllEmployees() throws Exception {
        String jsonResponse =
                """
                [
                    {
			            "id": "28a71512-247a-45e7-a135-47cf9663073f",
			            "employee_name": "Briana Schiller",
			            "employee_salary": 222890,
			            "employee_age": 58,
			            "employee_title": "Mining Officer",
			            "employee_email": "fixsan@company.com"
			        },
			        {
			            "id": "3f21802a-446e-4383-a414-666e295af10a",
			            "employee_name": "Mrs. Chung Howell",
			            "employee_salary": 225208,
			            "employee_age": 26,
			            "employee_title": "Principal Design Producer",
			            "employee_email": "zontrax@company.com"
			        }
                ]
                """;
        when(employeeService.getAllEmployees()).thenReturn(employees);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/employeechallenge"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(
                jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    void shouldReturnEmptyListOfEmployeesWhenNoData() throws Exception {
        String jsonResponse = "[]";
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());

        ResultActions resultActions = mockMvc.perform(get("/api/v1/employeechallenge"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(
                jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    void shouldReturnInternalServerProcessingWhenErrorInApi() throws Exception {
        when(employeeService.getAllEmployees()).thenThrow(new EmployeeServiceException());
        mockMvc.perform(get("/api/v1/employeechallenge")).andExpect(status().isInternalServerError());

        when(employeeService.findByName("brian")).thenThrow(new EmployeeServiceException());
        mockMvc.perform(get("/api/v1/employeechallenge/search/brian")).andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnAllEmployeesWithGivenNameSearch() throws Exception {
        String jsonResponse =
                """
                [
                    {
			            "id": "28a71512-247a-45e7-a135-47cf9663073f",
			            "employee_name": "Briana Schiller",
			            "employee_salary": 222890,
			            "employee_age": 58,
			            "employee_title": "Mining Officer",
			            "employee_email": "fixsan@company.com"
			        }
                ]
                """;
        when(employeeService.findByName("brian")).thenReturn(employees.subList(0, 1));

        ResultActions resultActions = mockMvc.perform(get("/api/v1/employeechallenge/search/brian"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(
                jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    void shouldReturnEmptyListWithGivenNameSearch() throws Exception {
        String jsonResponse = "[]";
        when(employeeService.findByName("brian")).thenReturn(Collections.emptyList());

        ResultActions resultActions = mockMvc.perform(get("/api/v1/employeechallenge/search/brian"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(
                jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    void shouldReturnAnEmployeeWithGivenId() throws Exception {

        String jsonResponse = EmployeeView.toJson(employees.get(0));
        when(employeeService.findById("28a71512-247a-45e7-a135-47cf9663073f")).thenReturn(employees.get(0));

        ResultActions resultActions = mockMvc.perform(get("/api/v1/employeechallenge/28a71512-247a-45e7-a135-47cf9663073f"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(
                jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    void shouldReturnEmployeeNotFoundWithGivenId() throws Exception {

        when(employeeService.findById("28a71512-247a-45e7-a135-47cf9663073f"))
                .thenThrow(EmployeeNotFoundException.class);
        mockMvc.perform(get("/api/v1/employeechallenge/28a71512-247a-45e7-a135-47cf9663073f"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnHighestSalaryOfEmployees() throws Exception {
        String response = Integer.valueOf(225208).toString();
        when(employeeService.findHighestSalaryOfEmployees()).thenReturn(Integer.valueOf(225208));
        ResultActions resultActions = mockMvc.perform(get("/api/v1/employeechallenge/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().json(response));
        JSONAssert.assertEquals(
                response, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    void shouldReturnNoContentFoundException() throws Exception {
        when(employeeService.findHighestSalaryOfEmployees()).thenThrow(NoContentFoundException.class);
        mockMvc.perform(get("/api/v1/employeechallenge/highestSalary")).andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnTopTenHighestEarningEmployeeNames() throws Exception {
        List<EmployeeView> allEmployees = new ArrayList<>();
        for (int i = 20; i > 0; i--) {
            allEmployees.add(EmployeeView.from(
                    employees.get(0),
                    employees.get(0).salary() + i,
                    employees.get(0).name() + i));
        }
        List<String> topTenHighestEarningEmployeeNames =
                allEmployees.stream().limit(10).map(e -> e.name()).collect(Collectors.toList());

        JSONArray expectedNames = new JSONArray(topTenHighestEarningEmployeeNames);
        when(employeeService.findTopTenHighestEarningEmployeeNames()).thenReturn(topTenHighestEarningEmployeeNames);
        ResultActions resultActions = mockMvc.perform(get("/api/v1/employeechallenge/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedNames.toString()));
        JSONAssert.assertEquals(
                expectedNames.toString(),
                resultActions.andReturn().getResponse().getContentAsString(),
                false);
    }

    @Test
    void shouldCreateEmployee() throws Exception {
        CreateEmployeeRequest employee =
                new CreateEmployeeRequest("Briana Schiller", 222890, 58, "Mining Officer", "fixsan@company.com");
        String employeeCreateRequest = CreateEmployeeRequest.toJson(employee);

        String jsonResponse = EmployeeView.toJson(employees.get(0));
        when(employeeService.createEmployee(employee)).thenReturn(employees.get(0));

        mockMvc.perform(post("/api/v1/employeechallenge").contentType("application/json").content(employeeCreateRequest))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void shouldThrowBadRequestWhenNameIsBlank() throws Exception {
        CreateEmployeeRequest employee =
                new CreateEmployeeRequest("", 222890, 58, "Mining Officer", "fixsan@company.com");
        shouldThrowBadRequest(employee);
    }

    @Test
    void shouldThrowBadRequestWhenSalaryIsNegative() throws Exception {
        CreateEmployeeRequest employee =
                new CreateEmployeeRequest("Briana Schiller", -222890, 58, "Mining Officer", "fixsan@company.com");
        shouldThrowBadRequest(employee);
    }

    @Test
    void shouldThrowBadRequestWhenSalaryIsBlank() throws Exception {
        CreateEmployeeRequest employee =
                new CreateEmployeeRequest("Briana Schiller", null, 58, "Mining Officer", "fixsan@company.com");
        shouldThrowBadRequest(employee);
    }

    @Test
    void shouldThrowBadRequestWhenAgeIsBelow16() throws Exception {
        CreateEmployeeRequest employee =
                new CreateEmployeeRequest("Briana Schiller", 222890, 5, "Mining Officer", "fixsan@company.com");
        shouldThrowBadRequest(employee);
    }

    @Test
    void shouldThrowBadRequestWhenAgeIsAbove75() throws Exception {
        CreateEmployeeRequest employee =
                new CreateEmployeeRequest("Briana Schiller", 222890, 85, "Mining Officer", "fixsan@company.com");
        shouldThrowBadRequest(employee);
    }

    @Test
    void shouldThrowBadRequestWhenTitleIsBlank() throws Exception {
        CreateEmployeeRequest employee =
                new CreateEmployeeRequest("Briana Schiller", 222890, 75, "", "fixsan@company.com");
        shouldThrowBadRequest(employee);
    }

    @Test
    void shouldThrowBadRequestWhenEmailIsBlank() throws Exception {
        CreateEmployeeRequest employee = new CreateEmployeeRequest("Briana Schiller", 222890, 75, "", "");
        shouldThrowBadRequest(employee);
    }

    @Test
    void shouldThrowBadRequestWhenEmailIsInvalid() throws Exception {
        CreateEmployeeRequest employee = new CreateEmployeeRequest("Briana Schiller", 222890, 75, "", "abc");
        shouldThrowBadRequest(employee);
    }

    private void shouldThrowBadRequest(CreateEmployeeRequest employee) throws Exception {
        String employeeCreateRequest = CreateEmployeeRequest.toJson(employee);
        mockMvc.perform(post("/api/v1/employeechallenge").contentType("application/json").content(employeeCreateRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteEmployeeById() throws Exception {

        when(employeeService.findById("28a71512-247a-45e7-a135-47cf9663073f")).thenReturn(employees.get(0));
        when(employeeService.findByName("Briana Schiller")).thenReturn(employees.subList(0, 1));
        when(employeeService.deleteEmployeeById("28a71512-247a-45e7-a135-47cf9663073f"))
                .thenReturn("Deleted");
        mockMvc.perform(delete("/api/v1/employeechallenge/28a71512-247a-45e7-a135-47cf9663073f"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted"));
    }

    @Test
    void shouldThrowConflictExceptionForDeleteEmployeeById() throws Exception {
        when(employeeService.deleteEmployeeById("28a71512-247a-45e7-a135-47cf9663073f"))
                .thenThrow(new MultipleEmployeeConflictInDeleteOperationException());
        mockMvc.perform(delete("/api/v1/employeechallenge/28a71512-247a-45e7-a135-47cf9663073f"))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldThrowEmployeeNotFoundExceptionDeleteEmployeeById() throws Exception {
        when(employeeService.deleteEmployeeById("28a71512-247a-45e7-a135-47cf9663073f"))
                .thenThrow(new EmployeeNotFoundException());
        mockMvc.perform(delete("/api/v1/employeechallenge/28a71512-247a-45e7-a135-47cf9663073f"))
                .andExpect(status().isNotFound());
    }
}
