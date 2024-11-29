package com.reliaquest.api.exception;

import com.reliaquest.api.dto.APIError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the application
 *
 * @author Saurabh
 */
@Slf4j
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(EmployeeServiceIntegrationException.class)
    public ResponseEntity<APIError> handleEmployeeServiceIntegrationException(EmployeeServiceIntegrationException ex) {
        APIError apiError = new APIError();
        apiError.setError(ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<APIError> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        APIError apiError = new APIError();
        apiError.setError(ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIError> handleEmployeeNotFoundException(IllegalArgumentException ex) {
        APIError apiError = new APIError();
        apiError.setError(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<APIError> handleTooManyRequestsException(TooManyRequestsException ex) {
        APIError apiError = new APIError();
        apiError.setError(ex.getMessage());

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIError> handleException(Exception ex) {
        APIError apiError = new APIError();
        apiError.setError("An Internal error has occurred. Please contact api-support@company.com");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }
}
