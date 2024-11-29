package com.reliaquest.api.exception;

/**
 * This exception is thrown when some error is encountered while calling external Employee service
 *
 * @author Saurabh
 */
public class EmployeeServiceIntegrationException extends RuntimeException {

    public EmployeeServiceIntegrationException(String message) {
        super(message);
    }
}
