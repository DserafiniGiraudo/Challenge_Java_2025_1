package com.federico.negocio.app.auth_service.exceptions;


public class UserAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private UserAlreadyExistsException(String message) {
        super(message);
    }

    public static UserAlreadyExistsException builder() {
        return new UserAlreadyExistsException(ConstantsExceptions.INVALID_CREDENTIALS);
    }
}