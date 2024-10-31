package com.example.redisson_sample.exception;

public class CustomException extends RuntimeException{

    public CustomException(String message) {
        super(message);
    }
}
