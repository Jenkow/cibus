package com.jll.cibus.common.exception;

import org.hibernate.transform.AliasToBeanConstructorResultTransformer;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String field, Object value) {
        super("Resource " + field + " with value " + value + " already exists");
    }
}
