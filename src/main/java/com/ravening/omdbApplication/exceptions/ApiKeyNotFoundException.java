package com.ravening.omdbApplication.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ApiKeyNotFoundException extends RuntimeException {

    public ApiKeyNotFoundException() {
        super("Api key not found in the request");
    }
}
