package com.jll.cibus.common.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String resourceName, Long resourceId) {
        super("Resource " + resourceName + " with ID " + resourceId + " already exists");
    }
}
