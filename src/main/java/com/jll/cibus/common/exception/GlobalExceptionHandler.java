package com.jll.cibus.common.exception;

import com.jll.cibus.common.errordetails.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex){
        ErrorDetails errorDetails = new ErrorDetails("MethodArgumentNotValidException", ex.getMessage(), HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorDetails> businessExceptionHandler(BusinessException ex){
        ErrorDetails errorDetails = new ErrorDetails("BusinessException", ex.getMessage(), HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(DuplicateAttributeException.class)
    public ResponseEntity<ErrorDetails> duplicateAttributeExceptionHandler(DuplicateAttributeException ex){
        ErrorDetails errorDetails = new ErrorDetails("DuplicateAttributeException", ex.getMessage(), HttpStatus.CONFLICT.value());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorDetails> invalidCredentialsExceptionHandler(InvalidCredentialsException ex){
        ErrorDetails errorDetails = new ErrorDetails("InvalidCredentialsException", ex.getMessage(), HttpStatus.UNAUTHORIZED.value());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> resourceAlreadyExistsExceptionHandler(ResourceAlreadyExistsException ex){
        ErrorDetails errorDetails = new ErrorDetails("ResourceAlreadyExistsException", ex.getMessage(), HttpStatus.CONFLICT.value());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> resourceNotFoundExceptionHandler(ResourceNotFoundException ex){
        ErrorDetails errorDetails = new ErrorDetails("ResourceNotFoundException", ex.getMessage(), HttpStatus.NOT_FOUND.value());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<ErrorDetails> unauthorizedOperationExceptionHandler(UnauthorizedOperationException ex){
        ErrorDetails errorDetails = new ErrorDetails("UnauthorizedOperationException", ex.getMessage(), HttpStatus.UNAUTHORIZED.value());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
    }
}
