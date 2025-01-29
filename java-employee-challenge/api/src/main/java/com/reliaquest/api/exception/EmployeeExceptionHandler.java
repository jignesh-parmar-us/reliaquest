package com.reliaquest.api.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class EmployeeExceptionHandler {

    @ExceptionHandler(EmployeeServiceException.class)
    protected ResponseEntity<?> handleException(EmployeeServiceException ex) {
        log.error("Error Status in Response Employee Service", ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    protected ResponseEntity<?> handleException(FeignException ex) {
        log.error("Exception in Feign Client", ex);
        return ResponseEntity.internalServerError().body(null);
    }
}
