package com.iuh.edu.fit.BEJewelry.Architecture.util.error.exception;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}