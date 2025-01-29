package com.reliaquest.api.data;

import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmployeeDataServiceClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 404: {
                log.error("Resource Not Found for {} service", response.request());
                return new EmployeeNotFoundException();
            }
            case 429: {
                log.error("Too Many Request for {} service ", response.request());
                return new EmployeeServiceException();
            }
            default:
                log.error("Exception From Employee Mock Server for {} service", response.request());
                return new EmployeeServiceException();
        }
    }
}
