package com.epam.nazar.grinko.exceptions;

import org.springframework.security.core.AuthenticationException;

public class IllegalJwtContentException extends AuthenticationException {

    public IllegalJwtContentException() {
        super("The content of the token does not match the content of the authentication token");
    }

    public IllegalJwtContentException(String msg) {
        super(msg);
    }

}
