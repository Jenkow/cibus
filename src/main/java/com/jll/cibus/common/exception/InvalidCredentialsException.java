package com.jll.cibus.common.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super("Invalid credentials for operation");
    }
}
