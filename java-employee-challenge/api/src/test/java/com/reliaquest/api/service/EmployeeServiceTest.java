package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.reliaquest.api.data.EmployeeDataServiceClient;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceException;
import com.reliaquest.api.exception.MultipleEmployeeConflictInDeleteOperationException;
import com.reliaquest.api.exception.NoContentFoundException;
import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.DeleteEmployeeRequest;
import com.reliaquest.api.model.EmployeeDataServiceResponse;
import com.reliaquest.api.model.EmployeeDataServiceResponse.Status;
import com.reliaquest.api.model.EmployeeEntity;
import com.reliaquest.api.model.EmployeeView;
import com.reliaquest.api.service.impl.EmployeeServiceImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = EmployeeServiceImpl.class)
@ExtendWith(SpringExtension.class)
public class EmployeeServiceTest {

    @MockBean
    EmployeeDataServiceClient employeeServiceClient;

    @Autowired
    EmployeeService employeeService;

    List<EmployeeView> employeeViews;
    List<EmployeeEntity> employeeEntities;

    @BeforeEach
    void setup() {
        employeeEntities = List.of(
                new EmployeeEntity(
                        "28a71512-247a-45e7-a135-47cf9663073f",
                        "Briana Schiller",
                        222890,
                        58,
                        "Mining Officer",
                        "fixsan@company.com"),
                new EmployeeEntity(
                        "3f21802a-446e-4383-a414-666e295af10a",
                        "Mrs. Chung Howell",
                        225208,
                        26,
                        "Principal Design Producer",
                        "zontrax@company.com"));
        employeeViews = employeeEntities.stream().map(EmployeeView::from).collect(Collectors.toList());
    }

    @Test
    void shouldReturnAllEmployees() throws Exception {
        EmployeeDataServiceResponse<List<EmployeeEntity>> employeeListResponse =
                new EmployeeDataServiceResponse<List<EmployeeEntity>>(employeeEntities, Status.HANDLED, null);
        when(employeeServiceClient.getAllEmployees()).thenReturn(employeeListResponse);
        List<EmployeeView> getAllEmployeeViews = employeeService.getAllEmployees();
        assertIterableEquals(getAllEmployeeViews, employeeViews);
    }

    @Test
    void shouldReturnStatusHandled() {
        assertTrue(EmployeeServiceImpl.isStatusHandled(Status.HANDLED));
    }

    @Test
    void shouldReturnStatusError() {
        assertFalse(EmployeeServiceImpl.isStatusHandled(Status.ERROR));
    }

    @Test
    void shouldReturnEmptyListOfEmployees() throws Exception {
        EmployeeDataServiceResponse<List<EmployeeEntity>> employeeListResponse =
                new EmployeeDataServiceResponse<List<EmployeeEntity>>(Collections.emptyList(), Status.HANDLED, null);
        when(employeeServiceClient.getAllEmployees()).thenReturn(employeeListResponse);
        List<EmployeeView> getAllEmployeeViews = employeeService.getAllEmployees();
        assertIterableEquals(getAllEmployeeViews, Collections.emptyList());
    }

    @Test
    void shouldReturnExceptionFromEmployeeService() throws Exception {
        EmployeeDataServiceResponse<List<EmployeeEntity>> employeeListResponse =
                new EmployeeDataServiceResponse<List<EmployeeEntity>>(null, Status.ERROR, null);
        when(employeeServiceClient.getAllEmployees()).thenReturn(employeeListResponse);
        assertThrows(EmployeeServiceException.class, employeeService::getAllEmployees);
    }

    @Test
    void shouldReturnAllEmployeesWithGivenNameSearch() throws Exception {
        EmployeeDataServiceResponse<List<EmployeeEntity>> employeeListResponse =
                new EmployeeDataServiceResponse<List<EmployeeEntity>>(employeeEntities, Status.HANDLED, null);
        when(employeeServiceClient.getAllEmployees()).thenReturn(employeeListResponse);
        List<EmployeeView> getAllEmployeeViews = employeeService.findByName("brian");
        assertIterableEquals(getAllEmployeeViews, employeeViews.subList(0, 1));
    }

    @Test
    void shouldReturnEmptyListWithGivenNameSearch() throws Exception {
        EmployeeDataServiceResponse<List<EmployeeEntity>> employeeListResponse =
                new EmployeeDataServiceResponse<List<EmployeeEntity>>(employeeEntities, Status.HANDLED, null);
        when(employeeServiceClient.getAllEmployees()).thenReturn(employeeListResponse);
        List<EmployeeView> getAllEmployeeViews = employeeService.findByName("NotFound");
        assertIterableEquals(getAllEmployeeViews, Collections.emptyList());
    }

    @Test
    void shouldReturnAnEmployeeWithGivenId() throws Exception {
        String uuid = "28a71512-247a-45e7-a135-47cf9663073f";
        EmployeeDataServiceResponse<EmployeeEntity> employeeListResponse =
                new EmployeeDataServiceResponse<EmployeeEntity>(employeeEntities.get(0), Status.HANDLED, null);
        when(employeeServiceClient.getEmployee(UUID.fromString(uuid))).thenReturn(employeeListResponse);
        EmployeeView employeeView = employeeService.findById(uuid);
        assertEquals(employeeViews.get(0), employeeView);
    }

    @Test
    void shouldReturnEmployeeNotFoundWithGivenId() throws Exception {
        String uuid = "28a71512-247a-45e7-a135-47cf9663073f";
        EmployeeDataServiceResponse<EmployeeEntity> employeeListResponse =
                new EmployeeDataServiceResponse<EmployeeEntity>(null, Status.ERROR, null);
        when(employeeServiceClient.getEmployee(UUID.fromString(uuid))).thenReturn(employeeListResponse);
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.findById(uuid));
    }

    @Test
    void shouldReturnIllegalArgumentException() throws Exception {
        String uuid = "28a7151";
        assertThrows(IllegalArgumentException.class, () -> employeeService.findById(uuid));
    }

    @Test
    void shouldReturnHighestSalaryOfEmployees() throws Exception {
        Integer expectedSalary = Integer.valueOf(225208);
        EmployeeDataServiceResponse<List<EmployeeEntity>> employeeListResponse =
                new EmployeeDataServiceResponse<List<EmployeeEntity>>(employeeEntities, Status.HANDLED, null);
        when(employeeServiceClient.getAllEmployees()).thenReturn(employeeListResponse);
        Integer actualSalary = employeeService.findHighestSalaryOfEmployees();
        assertEquals(expectedSalary, actualSalary);
    }

    @Test
    void shouldThrowNoContentFoundException() throws Exception {
        EmployeeDataServiceResponse<List<EmployeeEntity>> employeeListResponse =
                new EmployeeDataServiceResponse<List<EmployeeEntity>>(Collections.emptyList(), Status.HANDLED, null);
        when(employeeServiceClient.getAllEmployees()).thenReturn(employeeListResponse);
        assertThrows(NoContentFoundException.class, employeeService::findHighestSalaryOfEmployees);
    }

    @Test
    void shouldReturnTopTenHighestEarningEmployeeNames() throws Exception {
        List<EmployeeEntity> allEmployees = new ArrayList<>();
        for (int i = 20; i > 0; i--) {
            allEmployees.add(EmployeeEntity.from(
                    employeeEntities.get(0),
                    employeeEntities.get(0).salary() + i,
                    employeeEntities.get(0).name() + i));
        }
        List<String> expectedTopTenHighestEarningEmployeeNames =
                allEmployees.stream().limit(10).map(e -> e.name()).collect(Collectors.toList());
        EmployeeDataServiceResponse<List<EmployeeEntity>> employeeListResponse =
                new EmployeeDataServiceResponse<List<EmployeeEntity>>(allEmployees, Status.HANDLED, null);
        when(employeeServiceClient.getAllEmployees()).thenReturn(employeeListResponse);
        List<String> actualTopTenHighestEarningEmployeeNames = employeeService.findTopTenHighestEarningEmployeeNames();
        assertIterableEquals(expectedTopTenHighestEarningEmployeeNames, actualTopTenHighestEarningEmployeeNames);
    }

    @Test
    void shouldReturnAnEmployeeWhenCreated() throws Exception {
        CreateEmployeeRequest employee =
                new CreateEmployeeRequest("Briana Schiller", 222890, 58, "Mining Officer", "fixsan@company.com");
        EmployeeEntity employeeEntity = CreateEmployeeRequest.to(employee);
        EmployeeView expectedEmployeeView = EmployeeView.from(employeeEntity);
        EmployeeDataServiceResponse<EmployeeEntity> employeeResponse =
                new EmployeeDataServiceResponse<EmployeeEntity>(employeeEntity, Status.HANDLED, null);
        when(employeeServiceClient.createEmployee(employee)).thenReturn(employeeResponse);
        EmployeeView actualEmployeeView = employeeService.createEmployee(employee);
        assertEquals(expectedEmployeeView, actualEmployeeView);
    }

    @Test
    void shouldDeleteEmployeeById() throws Exception {
        String uuid = "28a71512-247a-45e7-a135-47cf9663073f";
        EmployeeDataServiceResponse<EmployeeEntity> employeeListResponse =
                new EmployeeDataServiceResponse<EmployeeEntity>(employeeEntities.get(0), Status.HANDLED, null);
        when(employeeServiceClient.getEmployee(UUID.fromString(uuid))).thenReturn(employeeListResponse);

        EmployeeDataServiceResponse<List<EmployeeEntity>> employeesByName =
                new EmployeeDataServiceResponse<List<EmployeeEntity>>(employeeEntities, Status.HANDLED, null);
        when(employeeServiceClient.getAllEmployees()).thenReturn(employeesByName);

        DeleteEmployeeRequest deleteEmployeeRequest = new DeleteEmployeeRequest("Briana Schiller");
        EmployeeDataServiceResponse<Boolean> deleteResponse =
                new EmployeeDataServiceResponse<Boolean>(true, Status.HANDLED, null);
        when(employeeServiceClient.deleteEmployee(deleteEmployeeRequest)).thenReturn(deleteResponse);
        String expectedResponse = "Deleted";
        String actualResponse = employeeService.deleteEmployeeById(uuid);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void shouldThrowConflictExceptionForDeleteEmployeeById() throws Exception {
        String uuid = "28a71512-247a-45e7-a135-47cf9663073f";
        EmployeeDataServiceResponse<EmployeeEntity> employeeListResponse =
                new EmployeeDataServiceResponse<EmployeeEntity>(employeeEntities.get(0), Status.HANDLED, null);
        when(employeeServiceClient.getEmployee(UUID.fromString(uuid))).thenReturn(employeeListResponse);
        List<EmployeeEntity> duplicateEmployeeNameEntities = new ArrayList<>();
        duplicateEmployeeNameEntities.addAll(employeeEntities);
        duplicateEmployeeNameEntities.add(new EmployeeEntity(
                "28a71512-247a-45e7-a135-47cf9663073d",
                "Briana Schiller",
                222890,
                58,
                "Mining Officer",
                "fixsan@company.com"));
        EmployeeDataServiceResponse<List<EmployeeEntity>> employeesByName = new EmployeeDataServiceResponse<
                List<EmployeeEntity>>(duplicateEmployeeNameEntities, Status.HANDLED, null);
        when(employeeServiceClient.getAllEmployees()).thenReturn(employeesByName);
        assertThrows(
                MultipleEmployeeConflictInDeleteOperationException.class,
                () -> employeeService.deleteEmployeeById(uuid));
    }

    @Test
    void shouldThrowEmployeeNotFoundWhenDeleteEmployeeById() throws Exception {
        String uuid = "28a71512-247a-45e7-a135-47cf9663073f";
        EmployeeDataServiceResponse<EmployeeEntity> employeeListResponse =
                new EmployeeDataServiceResponse<EmployeeEntity>(null, Status.ERROR, null);
        when(employeeServiceClient.getEmployee(UUID.fromString(uuid))).thenReturn(employeeListResponse);
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployeeById(uuid));
    }
}
