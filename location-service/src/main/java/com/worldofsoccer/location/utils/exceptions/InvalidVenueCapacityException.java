package com.worldofsoccer.location.utils.exceptions;

public class InvalidVenueCapacityException extends RuntimeException {

    public InvalidVenueCapacityException() {
        super();
    }

    public InvalidVenueCapacityException(String message) {
        super(message);
    }

    public InvalidVenueCapacityException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidVenueCapacityException(Throwable cause) {
        super(cause);
    }
}
