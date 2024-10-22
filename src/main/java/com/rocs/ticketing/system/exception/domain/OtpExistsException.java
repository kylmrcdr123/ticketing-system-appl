package com.rocs.ticketing.system.exception.domain;

public class OtpExistsException extends RuntimeException {
    public OtpExistsException(String message) {
        super(message);
    }
}
