package com.reliaquest.api.service;

import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.EmployeeView;
import java.util.List;

public interface EmployeeService {
    List<EmployeeView> getAllEmployees();

    List<EmployeeView> findByName(String searchString);

    EmployeeView findById(String id);

    Integer findHighestSalaryOfEmployees();

    List<String> findTopTenHighestEarningEmployeeNames();

    EmployeeView createEmployee(CreateEmployeeRequest employeeRequestBody);

    String deleteEmployeeById(String name);
}
