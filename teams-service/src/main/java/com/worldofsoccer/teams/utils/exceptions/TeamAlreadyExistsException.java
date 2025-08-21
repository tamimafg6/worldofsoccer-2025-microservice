package com.worldofsoccer.teams.utils.exceptions;

public class TeamAlreadyExistsException extends RuntimeException {

    public TeamAlreadyExistsException() {
        super();
    }

    public TeamAlreadyExistsException(String message) {
        super(message);
    }

    public TeamAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TeamAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}