package com.phelti.frontend.webservicesclients;

public class BackendApiException extends RuntimeException {
    private final int statusCode;

    public BackendApiException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() { return statusCode; }
}
