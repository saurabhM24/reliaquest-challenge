package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.config.AppConfig;
import com.reliaquest.api.dto.CreateEmployeeRequestDto;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.external.EmployeeServiceIntegration;
import com.reliaquest.api.external.dto.CreateEmployeeResponseDto;
import com.reliaquest.api.external.dto.DeleteEmployeeRequestDto;
import com.reliaquest.api.external.dto.DeleteEmployeeResponseDto;
import com.reliaquest.api.external.dto.GetAllEmployeesResponseDto;
import com.reliaquest.api.external.dto.GetEmployeeResponseDto;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceIntegrationTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private AppConfig appConfig;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private EmployeeServiceIntegration employeeServiceIntegration;

    private static final String BASE_URL = "http://localhost/8080";
    private static final String RESOURCE_URL = "/employee";

    @BeforeEach
    void setUp() {
        // Mockito.when(webClientBuilder.baseUrl(Mockito.anyString())).thenReturn(webClientBuilder);
        // Mockito.when(webClientBuilder.build()).thenReturn(webClient);
        Mockito.when(appConfig.getEmployeeServiceBaseUrl()).thenReturn(BASE_URL);
        Mockito.when(appConfig.getEmployeeServiceResourceUrl()).thenReturn(RESOURCE_URL);
    }

    @Test
    void getAllEmployees_ValidData_ReturnsSuccess() {
        GetAllEmployeesResponseDto mockResponse = new GetAllEmployeesResponseDto();
        mockResponse.setData(Arrays.asList(new EmployeeDto(), new EmployeeDto()));

        ResponseEntity<GetAllEmployeesResponseDto> responseEntity = ResponseEntity.ok(mockResponse);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        GetAllEmployeesResponseDto result = employeeServiceIntegration.getAllEmployees();

        assertNotNull(result);
        assertEquals(2, result.getData().size());
    }

    @Test
    void getAllEmployees_RateLimit_ThrowsException() {
        GetAllEmployeesResponseDto mockResponse = new GetAllEmployeesResponseDto();
        mockResponse.setData(Arrays.asList(new EmployeeDto(), new EmployeeDto()));

        ResponseEntity<GetAllEmployeesResponseDto> responseEntity =
                ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(mockResponse);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        Assertions.assertThrows(RuntimeException.class, () -> {
            employeeServiceIntegration.getAllEmployees();
        });
    }

    @Test
    void getAllEmployees_BadRequest_ThrowsException() {
        GetAllEmployeesResponseDto mockResponse = new GetAllEmployeesResponseDto();
        mockResponse.setData(Arrays.asList(new EmployeeDto(), new EmployeeDto()));

        ResponseEntity<GetAllEmployeesResponseDto> responseEntity =
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockResponse);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        Assertions.assertThrows(RuntimeException.class, () -> {
            employeeServiceIntegration.getAllEmployees();
        });
    }

    @Test
    void getEmployeeById_ValidData_ReturnsSuccess() {
        GetEmployeeResponseDto mockResponse = new GetEmployeeResponseDto();
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setEmployeeName("Saurabh");
        mockResponse.setData(employeeDto);

        ResponseEntity<GetEmployeeResponseDto> responseEntity = ResponseEntity.ok(mockResponse);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        GetEmployeeResponseDto result =
                employeeServiceIntegration.getEmployeeById(UUID.fromString("9b4ae777-3df8-41ee-aabd-c603f43487dc"));

        assertNotNull(result);
        assertEquals("Saurabh", result.getData().getEmployeeName());
    }

    @Test
    void getEmployeeById_EmployeeNotFound_ThrowsEmployeeNotFoundException() {
        GetEmployeeResponseDto mockResponse = new GetEmployeeResponseDto();
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setEmployeeName("Saurabh");
        mockResponse.setData(employeeDto);

        ResponseEntity<GetEmployeeResponseDto> responseEntity =
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockResponse);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        Assertions.assertThrows(EmployeeNotFoundException.class, () -> {
            employeeServiceIntegration.getEmployeeById(UUID.fromString("9b4ae777-3df8-41ee-aabd-c603f43487dc"));
        });
    }

    @Test
    void deleteEmployeeByName_ValidData_ReturnsSuccess() {
        DeleteEmployeeResponseDto mockResponse = new DeleteEmployeeResponseDto();
        mockResponse.setData(true);
        ResponseEntity<DeleteEmployeeResponseDto> responseEntity = ResponseEntity.ok(mockResponse);

        when(webClient.method(HttpMethod.DELETE)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(Mockito.any(Mono.class), Mockito.eq(DeleteEmployeeRequestDto.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(Mockito.any())).thenReturn(Mono.just(responseEntity));

        DeleteEmployeeResponseDto result = employeeServiceIntegration.deleteEmployeeByName("Saurabh");

        assertNotNull(result);
        assertTrue(result.getData());
    }

    @Test
    public void testCreateEmployeeSuccess() {
        CreateEmployeeRequestDto requestDto = new CreateEmployeeRequestDto();
        requestDto.setName("Saurabh");
        CreateEmployeeResponseDto responseDto = new CreateEmployeeResponseDto();
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setEmployeeName("Saurabh");
        responseDto.setData(employeeDto);

        ResponseEntity<CreateEmployeeResponseDto> mockResponse = ResponseEntity.ok(responseDto);

        Mockito.when(webClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(Mockito.any(Mono.class), Mockito.eq(CreateEmployeeRequestDto.class)))
                .thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.exchangeToMono(Mockito.any())).thenReturn(Mono.just(mockResponse));

        CreateEmployeeResponseDto result = employeeServiceIntegration.createEmployee(requestDto);

        assertNotNull(result);
        assertEquals("Saurabh", result.getData().getEmployeeName());
    }
}
