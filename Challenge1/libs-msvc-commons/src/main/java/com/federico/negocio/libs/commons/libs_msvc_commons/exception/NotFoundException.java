package com.federico.negocio.libs.commons.libs_msvc_commons.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException{

    private static final long serialVersionUID = 198616516551L;

    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException build(String message) {
        return new NotFoundException(message);
    }
}
