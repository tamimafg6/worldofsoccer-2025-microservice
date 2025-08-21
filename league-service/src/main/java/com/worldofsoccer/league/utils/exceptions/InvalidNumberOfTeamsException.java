package com.worldofsoccer.league.utils.exceptions;

public class InvalidNumberOfTeamsException extends RuntimeException {

    public InvalidNumberOfTeamsException() {
        super();
    }

    public InvalidNumberOfTeamsException(String message) {
        super(message);
    }

    public InvalidNumberOfTeamsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidNumberOfTeamsException(Throwable cause) {
        super(cause);
    }
}