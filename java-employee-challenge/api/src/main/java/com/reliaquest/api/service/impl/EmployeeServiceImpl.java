package com.reliaquest.api.service.impl;

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
import com.reliaquest.api.service.EmployeeService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeDataServiceClient employeeServiceClient;

    Predicate<Status> isStatusHandledPredicate = (s) -> (0 == s.compareTo(Status.HANDLED)) ? true : false;

    static String DELETED_MESSAGE = "Deleted";

    @Override
    public List<EmployeeView> getAllEmployees() {
        List<EmployeeView> employees = new ArrayList<>();
        EmployeeDataServiceResponse<List<EmployeeEntity>> response = employeeServiceClient.getAllEmployees();
        if (isStatusHandled(response.status())) {
            employees = response.data().stream().map(EmployeeView::from).collect(Collectors.toList());
            log.debug("All employee: {}", employees);
        } else {
            throw new EmployeeServiceException();
        }
        return employees;
    }

    public static boolean isStatusHandled(Status response) {
        return (0 == response.compareTo(Status.HANDLED)) ? true : false;
    }

    @Override
    public List<EmployeeView> findByName(String searchString) {
        return getAllEmployees().stream()
                .filter(employee -> employee.name().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeView findById(String id) {
        EmployeeView employee;
        EmployeeDataServiceResponse<EmployeeEntity> response = employeeServiceClient.getEmployee(UUID.fromString(id));
        if (isStatusHandled(response.status())) {
            employee = EmployeeView.from(response.data());
            log.debug("Employee Found: {}", employee);
        } else {
            throw new EmployeeNotFoundException();
        }
        return employee;
    }

    @Override
    public Integer findHighestSalaryOfEmployees() {
        OptionalInt highestSalary =
                getAllEmployees().stream().mapToInt(EmployeeView::salary).max();
        return highestSalary.orElseThrow(NoContentFoundException::new);
    }

    @Override
    public List<String> findTopTenHighestEarningEmployeeNames() {
        return getAllEmployees().stream()
                .sorted(Comparator.comparingInt(EmployeeView::salary).reversed())
                .limit(10)
                .map(EmployeeView::name)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeView createEmployee(CreateEmployeeRequest employeeRequestBody) {
        EmployeeView employee;
        EmployeeDataServiceResponse<EmployeeEntity> response =
                employeeServiceClient.createEmployee(employeeRequestBody);
        if (isStatusHandled(response.status())) {
            employee = EmployeeView.from(response.data());
            log.debug("Employee Created: {}", employee);
        } else {
            throw new EmployeeServiceException();
        }
        return employee;
    }

    @Override
    public String deleteEmployeeById(String id) {
        EmployeeView employee = findById(id);
        log.debug("Employee Found: {}", employee);
        List<EmployeeView> employeesByName = findByName(employee.name());
        log.debug("Employee List By Name : {}", employeesByName);
        if (1 == employeesByName.size()
                && employee.id().equals(employeesByName.get(0).id())) {
            return deleteEmployeeByName(employee);
        } else if (employeesByName.size() > 1) {
            throw new MultipleEmployeeConflictInDeleteOperationException(
                    "Multiple employees available for delete with name - [" + employee.name() + "]");
        } else {
            throw new EmployeeNotFoundException("Employee with name - [" + employee.name() + "] not found");
        }
    }

    private String deleteEmployeeByName(EmployeeView employee) {
        EmployeeDataServiceResponse<Boolean> response =
                employeeServiceClient.deleteEmployee(new DeleteEmployeeRequest(employee.name()));
        if (isStatusHandled(response.status())) {
            if (response.data()) {
                log.debug("Employee Deleted : {}", employee);
                return DELETED_MESSAGE;
            } else {
                throw new EmployeeNotFoundException("Employee with name - [" + employee.name() + "] not found");
            }
        } else {
            throw new EmployeeServiceException();
        }
    }
}
