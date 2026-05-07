package com.jll.cibus.common.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Long resourceId) {
        super("Resource " + resourceName + " with ID " + resourceId + " does not exist");
    }
}
