package com.reliaquest.api.external;

import com.reliaquest.api.config.AppConfig;
import com.reliaquest.api.dto.CreateEmployeeRequestDto;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceIntegrationException;
import com.reliaquest.api.external.dto.CreateEmployeeResponseDto;
import com.reliaquest.api.external.dto.DeleteEmployeeRequestDto;
import com.reliaquest.api.external.dto.DeleteEmployeeResponseDto;
import com.reliaquest.api.external.dto.GetAllEmployeesResponseDto;
import com.reliaquest.api.external.dto.GetEmployeeResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

/**
 * This class contains the methods which make calls to external employee service
 *
 * @author Saurabh
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceIntegration {

    private final WebClient webClient;

    private final AppConfig appConfig;

    /**
     * Method to get All employees by calling external employee service
     * @return {@link GetAllEmployeesResponseDto}
     */
    public GetAllEmployeesResponseDto getAllEmployees() {
        String url = appConfig.getEmployeeServiceBaseUrl() + appConfig.getEmployeeServiceResourceUrl();
        log.info("Calling employee service at {} to get all employees", url);

        int maxAttempts = 4;

        for (int attempt = 0; attempt <= maxAttempts; attempt++) {
            try {
                ResponseEntity<GetAllEmployeesResponseDto> response = webClient
                        .get()
                        .uri(url)
                        .exchangeToMono(clientResponse -> clientResponse.toEntity(GetAllEmployeesResponseDto.class))
                        .block();

                if (response == null) {
                    log.error("Error occurred while fetching All employees data. Response is null");
                    throw new EmployeeServiceIntegrationException(
                            "Error occurred while fetching All employees data. Received Null response");
                }

                HttpStatus status = HttpStatus.valueOf(response.getStatusCode().value());
                switch (status) {
                    case OK:
                        GetAllEmployeesResponseDto getAllEmployeesResponseDto = response.getBody();
                        log.info(
                                "Fetched all employees successfully. Total number of employees fetched : {}",
                                getAllEmployeesResponseDto.getData().size());
                        return getAllEmployeesResponseDto;

                    case TOO_MANY_REQUESTS:
                        retryLogic(attempt, maxAttempts);
                        break;

                    default:
                        log.error("Error occurred while fetching All employees data. Status code returned: {}", status);
                        throw new EmployeeServiceIntegrationException(
                                "Error occurred while fetching All employees data. " + "Status code returned: "
                                        + status);
                }
            } catch (WebClientException e) {
                throw new EmployeeServiceIntegrationException(
                        "Error occurred in connecting with employee service. Please try again later.");
            }
        }

        return null;
    }

    /**
     * Method to get employees by ID by calling external employee service
     *
     * @return {@link GetEmployeeResponseDto}
     */
    public GetEmployeeResponseDto getEmployeeById(UUID id) {
        String url = appConfig.getEmployeeServiceBaseUrl() + appConfig.getEmployeeServiceResourceUrl() + "/" + id;

        log.info("Calling employee service at {} to get employee with id : {}", url, id);

        int maxAttempts = 4;

        for (int attempt = 0; attempt <= maxAttempts; attempt++) {
            try {
                ResponseEntity<GetEmployeeResponseDto> response = webClient
                        .get()
                        .uri(url)
                        .exchangeToMono(clientResponse -> clientResponse.toEntity(GetEmployeeResponseDto.class))
                        .block();

                if (response == null) {
                    log.error("Error occurred while fetching employee data with id: {}, Response is null", id);
                    throw new EmployeeServiceIntegrationException(
                            "Error occurred while fetching employee data with id : " + id + ", Received Null response");
                }

                HttpStatus status = HttpStatus.valueOf(response.getStatusCode().value());
                switch (status) {
                    case OK:
                        GetEmployeeResponseDto getEmployeeResponseDto = response.getBody();
                        log.info("Successfully fetched employee data with id : {}", id);
                        return getEmployeeResponseDto;

                    case TOO_MANY_REQUESTS:
                        retryLogic(attempt, maxAttempts);
                        break;

                    case NOT_FOUND:
                        throw new EmployeeNotFoundException("Employee with ID : " + id + " not found.");

                    case INTERNAL_SERVER_ERROR:
                    default:
                        throw new EmployeeServiceIntegrationException(
                                "Error occurred while fetching employees data with id. " + "Status code returned: "
                                        + status);
                }
            } catch (WebClientException e) {
                throw new EmployeeServiceIntegrationException(
                        "Error occurred in connecting with employee service. Please try again later.");
            }
        }

        return null;
    }

    /**
     * Method to delete employee by name by calling external employee service
     *
     * @param name Name of the employee to be deleted.
     * @return {@link DeleteEmployeeResponseDto}
     */
    public DeleteEmployeeResponseDto deleteEmployeeByName(String name) {
        String url = appConfig.getEmployeeServiceBaseUrl() + appConfig.getEmployeeServiceResourceUrl();

        log.info("Calling employee service at {} to delete employee with name : {}", url, name);

        DeleteEmployeeRequestDto deleteEmployeeRequestDto = new DeleteEmployeeRequestDto(name);

        try {
            int maxAttempts = 4;
            for (int attempt = 0; attempt <= maxAttempts; attempt++) {
                ResponseEntity<DeleteEmployeeResponseDto> response = webClient
                        .method(HttpMethod.DELETE)
                        .uri(url)
                        .body(Mono.just(deleteEmployeeRequestDto), DeleteEmployeeRequestDto.class)
                        .exchangeToMono(clientResponse -> clientResponse.toEntity(DeleteEmployeeResponseDto.class))
                        .block();

                HttpStatus status = HttpStatus.valueOf(response.getStatusCode().value());
                switch (status) {
                    case OK:
                        return response.getBody();

                    case TOO_MANY_REQUESTS:
                        retryLogic(attempt, maxAttempts);
                        break;

                    default:
                        log.error("Error occurred while deleting the employee. Status code returned: {}", status);
                        throw new EmployeeServiceIntegrationException(
                                "Error occurred while deleting the employees with name : " + name
                                        + ", Status code returned: " + status);
                }
            }
        } catch (WebClientException e) {
            throw new EmployeeServiceIntegrationException(
                    "Error occurred in connecting with employee service. Please try again later.");
        }

        return null;
    }

    /**
     * Method to create an Employee by calling external employee service
     * @param createEmployeeRequestDto {@link CreateEmployeeRequestDto}
     * @return {@link CreateEmployeeResponseDto}
     */
    public CreateEmployeeResponseDto createEmployee(CreateEmployeeRequestDto createEmployeeRequestDto) {
        String url = appConfig.getEmployeeServiceBaseUrl() + appConfig.getEmployeeServiceResourceUrl();

        log.info(
                "Calling employee service at {} to create employee with name : {}",
                url,
                createEmployeeRequestDto.getName());

        try {
            int maxAttempts = 4;
            for (int attempt = 0; attempt <= maxAttempts; attempt++) {
                ResponseEntity<CreateEmployeeResponseDto> response = webClient
                        .post()
                        .uri(url)
                        .body(Mono.just(createEmployeeRequestDto), CreateEmployeeRequestDto.class)
                        .exchangeToMono(clientResponse -> clientResponse.toEntity(CreateEmployeeResponseDto.class))
                        .block();

                HttpStatus status = HttpStatus.valueOf(response.getStatusCode().value());
                switch (status) {
                    case OK:
                        return response.getBody();

                    case TOO_MANY_REQUESTS:
                        retryLogic(attempt, maxAttempts);
                        break;

                    default:
                        log.error("Error occurred while creating the employee. Status code returned: {}", status);
                        throw new EmployeeServiceIntegrationException(
                                "Error occurred while creating the employees with name : "
                                        + createEmployeeRequestDto.getName()
                                        + ", Status code returned: " + status);
                }
            }
        } catch (WebClientException e) {
            throw new EmployeeServiceIntegrationException(
                    "Error occurred in connecting with employee service. Please try again later.");
        }

        return null;
    }

    private static void retryLogic(int attempt, int maxAttempts) {
        log.error("Attempt: {}, Received 429 Too many requests.", attempt);
        if (attempt < maxAttempts) {
            int sec = (int) Math.pow(2, attempt);
            log.info("Waiting for {} seconds before retrying.", sec);

            try {
                Thread.sleep(sec * 1000L);
            } catch (InterruptedException e) {
                log.error("Error occurred while waiting for {} seconds.", sec);
            }
        } else {
            throw new EmployeeServiceIntegrationException(
                    "Max retry exceeded due to 429 status. " + "Please try again later. System is under heavy load!!");
        }
    }
}
