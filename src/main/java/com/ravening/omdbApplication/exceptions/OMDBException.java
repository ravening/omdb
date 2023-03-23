package com.ravening.omdbApplication.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OMDBException extends RuntimeException{

    public OMDBException(Exception e) {
        super(e.getMessage());
    }
}
