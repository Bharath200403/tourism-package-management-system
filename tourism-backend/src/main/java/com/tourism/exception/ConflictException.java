package com.tourism.exception;

public class ConflictException extends RuntimeException {

    private final String code;

    public ConflictException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ConflictException(String message) {
        this(null, message);
    }

    public String getCode() {
        return code;
    }
}
