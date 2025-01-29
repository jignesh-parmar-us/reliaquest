# ReliaQuest  
ReliaQuest Interview Challenge  

## Implementation Details  

A new Employee Challenge API endpoint has been created at:  
**http://localhost:8111/api/v1/employeechallenge**  

### Features:  
- Implemented FeignClient for communication with the Mock Employee Service.  
- Followed Test-Driven Development (TDD) methodology.  
- **DeleteEmployeeById Logic:**  
  - Retrieve the Employee Name.  
  - Fetch all employees with this name.  
    - If multiple employees exist, return **HTTP 409 (Conflict)**.  
    - If only one employee exists, proceed with `deleteByName`.  
- Used **Resilience4j** for retry mechanisms.  

### Possible Enhancements (Not Implemented Due to Time Constraints):  
- Implement **CircuitBreaker** along with **Retry** to handle rate limiting.  
- Configure **Eureka Client** for service discovery.  
- Develop an **Integration Test Suite** for end-to-end testing.  

---  

Thank you for reviewing this! Looking forward to your feedback.  
