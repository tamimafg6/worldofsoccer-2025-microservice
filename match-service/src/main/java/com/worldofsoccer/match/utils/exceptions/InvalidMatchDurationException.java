package com.worldofsoccer.match.utils.exceptions;

public class InvalidMatchDurationException extends RuntimeException {

    public InvalidMatchDurationException() {
        super();
    }

    public InvalidMatchDurationException(String message) {
        super(message);
    }

    public InvalidMatchDurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMatchDurationException(Throwable cause) {
        super(cause);
    }
}