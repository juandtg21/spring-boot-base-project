package com.base.api.exception;

import org.springframework.security.core.AuthenticationException;

import java.io.Serial;

public class UserAlreadyExistAuthenticationException extends AuthenticationException {

    @Serial
    private static final long serialVersionUID = 5570981880007077317L;

    public UserAlreadyExistAuthenticationException(final String msg) {
        super(msg);
    }

}
