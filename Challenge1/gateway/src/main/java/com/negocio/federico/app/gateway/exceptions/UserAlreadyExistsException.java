package com.negocio.federico.app.gateway.exceptions;


public class UserAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private UserAlreadyExistsException(String message) {
        super(message);
    }

    public static UserAlreadyExistsException builder() {
        return new UserAlreadyExistsException(ConstantsExceptions.INVALID_CREDENTIALS);
    }
}