package com.jll.cibus.common.errordetails;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorDetails {

    private final LocalDateTime timestamp;
    private final Integer status;
    private final String error;
    private final String message;

    public ErrorDetails(String error, String message, Integer status){
        this.error = error;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
