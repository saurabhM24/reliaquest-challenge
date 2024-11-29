package com.reliaquest.api.exception;

/**
 * This exception is thrown when Employee is not found
 *
 * @author Saurabh
 */
public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
