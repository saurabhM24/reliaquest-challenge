package com.reliaquest.api.external;

import com.reliaquest.api.config.AppConfig;
import com.reliaquest.api.dto.CreateEmployeeRequestDto;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceIntegrationException;
import com.reliaquest.api.exception.TooManyRequestsException;
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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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
    @Retryable(
        value = {TooManyRequestsException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 30000))
    public GetAllEmployeesResponseDto getAllEmployees() {
        String url = appConfig.getEmployeeServiceBaseUrl() + appConfig.getEmployeeServiceResourceUrl();
        log.info("Calling employee service at {} to get all employees", url);

        try {
            ResponseEntity<GetAllEmployeesResponseDto> response = getAllEmployeesResponse(url);

            HttpStatus status = HttpStatus.valueOf(response.getStatusCode().value());
            switch (status) {
                case OK:
                    GetAllEmployeesResponseDto getAllEmployeesResponseDto = response.getBody();
                    log.info(
                            "Fetched all employees successfully. Total number of employees fetched : {}",
                            getAllEmployeesResponseDto.getData().size());
                    return getAllEmployeesResponseDto;

                case TOO_MANY_REQUESTS:
                    throw new TooManyRequestsException(
                            "Max retry exceeded due to 429 status. Please try again later. System is under heavy load!!");

                default:
                    log.error("Error occurred while fetching All employees data. Status code returned: {}", status);
                    throw new EmployeeServiceIntegrationException(
                            "Error occurred while fetching All employees data. " + "Status code returned: " + status);
            }
        } catch (WebClientException e) {
            throw new EmployeeServiceIntegrationException(
                    "Error occurred in connecting with employee service. Please try again later.");
        }
    }

    private ResponseEntity<GetAllEmployeesResponseDto> getAllEmployeesResponse(String url) {
        ResponseEntity<GetAllEmployeesResponseDto> response = webClient
                .get()
                .uri(url)
                .exchangeToMono(clientResponse -> clientResponse.toEntity(GetAllEmployeesResponseDto.class))
                .block();
        return response;
    }

    /**
     * Method to get employees by ID by calling external employee service
     *
     * @return {@link GetEmployeeResponseDto}
     */
    @Retryable(
        value = {TooManyRequestsException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 30000))
    public GetEmployeeResponseDto getEmployeeById(UUID id) {
        String url = appConfig.getEmployeeServiceBaseUrl() + appConfig.getEmployeeServiceResourceUrl() + "/" + id;

        log.info("Calling employee service at {} to get employee with id : {}", url, id);

        try {
            ResponseEntity<GetEmployeeResponseDto> response = getEmployeeByIdResponse(url);

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
                    throw new TooManyRequestsException(
                            "Max retry exceeded due to 429 status. Please try again later. System is under heavy load!!");

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

    private ResponseEntity<GetEmployeeResponseDto> getEmployeeByIdResponse(String url) {
        ResponseEntity<GetEmployeeResponseDto> response = webClient
                .get()
                .uri(url)
                .exchangeToMono(clientResponse -> clientResponse.toEntity(GetEmployeeResponseDto.class))
                .block();
        return response;
    }

    /**
     * Method to delete employee by name by calling external employee service
     *
     * @param name Name of the employee to be deleted.
     * @return {@link DeleteEmployeeResponseDto}
     */
    @Retryable(
        value = {TooManyRequestsException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 30000))
    public DeleteEmployeeResponseDto deleteEmployeeByName(String name) {
        String url = appConfig.getEmployeeServiceBaseUrl() + appConfig.getEmployeeServiceResourceUrl();

        log.info("Calling employee service at {} to delete employee with name : {}", url, name);

        DeleteEmployeeRequestDto deleteEmployeeRequestDto = new DeleteEmployeeRequestDto(name);

        try {
            // Calling external API
            ResponseEntity<DeleteEmployeeResponseDto> response = deleteEmployeeResponse(url, deleteEmployeeRequestDto);

            HttpStatus status = HttpStatus.valueOf(response.getStatusCode().value());
            switch (status) {
                case OK:
                    return response.getBody();

                case TOO_MANY_REQUESTS:
                    throw new TooManyRequestsException(
                            "Max retry exceeded due to 429 status. Please try again later. System is under heavy load!!");

                default:
                    log.error("Error occurred while deleting the employee. Status code returned: {}", status);
                    throw new EmployeeServiceIntegrationException(
                            "Error occurred while deleting the employees with name : " + name
                                    + ", Status code returned: " + status);
            }
        } catch (WebClientException e) {
            throw new EmployeeServiceIntegrationException(
                    "Error occurred in connecting with employee service. Please try again later.");
        }
    }

    private ResponseEntity<DeleteEmployeeResponseDto> deleteEmployeeResponse(
            String url, DeleteEmployeeRequestDto deleteEmployeeRequestDto) {
        ResponseEntity<DeleteEmployeeResponseDto> response = webClient
                .method(HttpMethod.DELETE)
                .uri(url)
                .body(Mono.just(deleteEmployeeRequestDto), DeleteEmployeeRequestDto.class)
                .exchangeToMono(clientResponse -> clientResponse.toEntity(DeleteEmployeeResponseDto.class))
                .block();
        return response;
    }

    /**
     * Method to create an Employee by calling external employee service
     * @param createEmployeeRequestDto {@link CreateEmployeeRequestDto}
     * @return {@link CreateEmployeeResponseDto}
     */
    @Retryable(
        value = {TooManyRequestsException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 30000))
    public CreateEmployeeResponseDto createEmployee(CreateEmployeeRequestDto createEmployeeRequestDto) {
        String url = appConfig.getEmployeeServiceBaseUrl() + appConfig.getEmployeeServiceResourceUrl();

        log.info(
                "Calling employee service at {} to create employee with name : {}",
                url,
                createEmployeeRequestDto.getName());

        try {
            ResponseEntity<CreateEmployeeResponseDto> response = createEmployeeResponse(createEmployeeRequestDto, url);

            HttpStatus status = HttpStatus.valueOf(response.getStatusCode().value());
            switch (status) {
                case OK:
                    return response.getBody();

                case TOO_MANY_REQUESTS:
                    throw new TooManyRequestsException(
                            "Max retry exceeded due to 429 status. Please try again later. System is under heavy load!!");

                default:
                    log.error("Error occurred while creating the employee. Status code returned: {}", status);
                    throw new EmployeeServiceIntegrationException(
                            "Error occurred while creating the employees with name : "
                                    + createEmployeeRequestDto.getName()
                                    + ", Status code returned: " + status);
            }
        } catch (WebClientException e) {
            throw new EmployeeServiceIntegrationException(
                    "Error occurred in connecting with employee service. Please try again later.");
        }
    }

    private ResponseEntity<CreateEmployeeResponseDto> createEmployeeResponse(
            CreateEmployeeRequestDto createEmployeeRequestDto, String url) {
        ResponseEntity<CreateEmployeeResponseDto> response = webClient
                .post()
                .uri(url)
                .body(Mono.just(createEmployeeRequestDto), CreateEmployeeRequestDto.class)
                .exchangeToMono(clientResponse -> clientResponse.toEntity(CreateEmployeeResponseDto.class))
                .block();
        return response;
    }
}
