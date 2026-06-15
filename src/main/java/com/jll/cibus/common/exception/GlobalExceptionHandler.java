package com.jll.cibus.common.exception;

import com.jll.cibus.common.errordetails.ErrorDetails;
import io.jsonwebtoken.JwtException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> iIllegalArgumentExceptionHandler(IllegalArgumentException ex){
        ErrorDetails errorDetails = new ErrorDetails("IllegalArgumentException", ex.getMessage(), HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorDetails> jwtException(JwtException ex){
        ErrorDetails errorDetails = new ErrorDetails("JwtException", ex.getMessage(), HttpStatus.UNAUTHORIZED.value());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
    }

    @ExceptionHandler (DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetails>
    dataIntegrityViolationHandler (DataIntegrityViolationException ex)
    {
        ErrorDetails errorDetails = new ErrorDetails("DataIntegrityViolationException","This operation violates the data integrity restriction", HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }
    @ExceptionHandler (AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> accesDeniedHandler (AccessDeniedException ex)
    {
        ErrorDetails errorDetails = new ErrorDetails("AccesDeniedException","You do not have permission to do this operation", HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetails);
    }
    @ExceptionHandler (AuthenticationException.class)
    public ResponseEntity<ErrorDetails> authenticationHandler (AuthenticationException ex)
    {
        ErrorDetails errorDetails = new ErrorDetails("AuthenticationException", "Invalid Credentials", HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
    }
    @ExceptionHandler (Exception.class)
    public ResponseEntity<ErrorDetails> genericExceptionHandler (Exception ex)
    {
        log.error("Unhandled error", ex);
        ErrorDetails errorDetails= new ErrorDetails("InternalServerError", "There was an unexpected mistake. Try again later", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }
}
