package com.reliaquest.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class NoContentFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoContentFoundException() {
        super();
    }

    public NoContentFoundException(String message) {
        super(message);
    }
}
