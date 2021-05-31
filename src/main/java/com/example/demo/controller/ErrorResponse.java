package com.example.demo.controller;

public class ErrorResponse implements java.io.Serializable {
    private java.lang.String status;
    private java.lang.String timestamp;
    private java.lang.String message;
    private java.lang.String debugMessage;

    public ErrorResponse() { /* compiled code */ }

    public ErrorResponse(org.springframework.http.HttpStatus status, java.lang.Throwable ex) {
        this.status = status.name();
        this.message = ex.getMessage();
        this.debugMessage = ex.getLocalizedMessage();
    }

}