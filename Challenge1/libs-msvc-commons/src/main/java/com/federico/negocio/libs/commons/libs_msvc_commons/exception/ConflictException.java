package com.federico.negocio.libs.commons.libs_msvc_commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

    private static final long serialVersionUID = 161986165551L;


    public ConflictException(String message) {
        super(message);
    }

    public static ConflictException build(String message) {
        return new ConflictException(message);
    }
}
