package com.microservices.ProductService.excpetion;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CustomException extends RuntimeException{

    private String error;
    private int status;

    public CustomException(String message, String error, int status) {
        super(message);
        this.error = error;
        this.status = status;
    }

    public CustomException(String message, int status) {
        super(message);
        this.status = status;
    }   
}