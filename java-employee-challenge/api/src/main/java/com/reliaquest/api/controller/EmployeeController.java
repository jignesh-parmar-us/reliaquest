package com.reliaquest.api.controller;

import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.EmployeeView;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employeechallenge")
public class EmployeeController implements IEmployeeController<EmployeeView, CreateEmployeeRequest> {

    @Autowired
    EmployeeService employeeService;

    @Override
    public ResponseEntity<List<EmployeeView>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Override
    public ResponseEntity<List<EmployeeView>> getEmployeesByNameSearch(
            @PathVariable("searchString") String searchString) {
        return ResponseEntity.ok(employeeService.findByName(searchString));
    }

    @Override
    public ResponseEntity<EmployeeView> getEmployeeById(@PathVariable("id") String id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return ResponseEntity.ok(employeeService.findHighestSalaryOfEmployees());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.ok(employeeService.findTopTenHighestEarningEmployeeNames());
    }

    @Override
    public ResponseEntity<EmployeeView> createEmployee(@Valid @RequestBody CreateEmployeeRequest employeeInput) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(employeeInput));
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") String id) {
        return ResponseEntity.ok(employeeService.deleteEmployeeById(id));
    }
}
