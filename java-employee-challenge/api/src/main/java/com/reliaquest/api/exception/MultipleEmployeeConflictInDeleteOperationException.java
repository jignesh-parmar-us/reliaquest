package com.reliaquest.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class MultipleEmployeeConflictInDeleteOperationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MultipleEmployeeConflictInDeleteOperationException() {
        super();
    }

    public MultipleEmployeeConflictInDeleteOperationException(String message) {
        super(message);
    }
}
