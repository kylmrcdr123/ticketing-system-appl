package com.rocs.ticketing.system.exception.domain;

public class PersonExistsException extends RuntimeException {
    public PersonExistsException(String message) {
        super(message);
    }
}
