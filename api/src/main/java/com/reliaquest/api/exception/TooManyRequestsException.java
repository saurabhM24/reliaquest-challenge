package com.reliaquest.api.exception;

/**
 *
 * @author Saurabh
 */
public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException(String message) {
        super(message);
    }
}
