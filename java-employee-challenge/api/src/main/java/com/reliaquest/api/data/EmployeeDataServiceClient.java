package com.reliaquest.api.data;

import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.DeleteEmployeeRequest;
import com.reliaquest.api.model.EmployeeDataServiceResponse;
import com.reliaquest.api.model.EmployeeEntity;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Retry(name = "simpleRetry")
@FeignClient(
        name = "employeeDataServiceClient",
        url = "${spring.cloud.openfeign.client.config.employeeDataServiceClient.url}")
public interface EmployeeDataServiceClient {

    @GetMapping("/api/v1/employee")
    EmployeeDataServiceResponse<List<EmployeeEntity>> getAllEmployees();

    @GetMapping("/api/v1/employee/{id}")
    EmployeeDataServiceResponse<EmployeeEntity> getEmployee(@PathVariable("id") UUID uuid);

    @PostMapping("/api/v1/employee")
    public EmployeeDataServiceResponse<EmployeeEntity> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request);

    @DeleteMapping("/api/v1/employee")
    public EmployeeDataServiceResponse<Boolean> deleteEmployee(@Valid @RequestBody DeleteEmployeeRequest input);
}
