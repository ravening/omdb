package com.ravening.omdbApplication.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(String title) {
        super(String.format("Movie with title %s not found in OMDB", title));
    }
}
