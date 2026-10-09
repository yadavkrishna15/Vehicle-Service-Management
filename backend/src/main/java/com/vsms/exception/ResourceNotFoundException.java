package com.vsms.exception;

/**
 * Custom runtime exception thrown when an entity is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
