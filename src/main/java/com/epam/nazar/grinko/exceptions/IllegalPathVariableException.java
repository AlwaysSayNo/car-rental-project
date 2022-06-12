package com.epam.nazar.grinko.exceptions;

import org.springframework.security.core.AuthenticationException;

public class IllegalPathVariableException extends AuthenticationException {
    public IllegalPathVariableException() {
        super("Path variable is invalid");
    }

    public IllegalPathVariableException(String msg) {
        super(msg);
    }
}
