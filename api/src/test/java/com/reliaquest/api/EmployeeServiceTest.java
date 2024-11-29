package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.dto.CreateEmployeeRequestDto;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.external.EmployeeServiceIntegration;
import com.reliaquest.api.external.dto.CreateEmployeeResponseDto;
import com.reliaquest.api.external.dto.DeleteEmployeeResponseDto;
import com.reliaquest.api.external.dto.GetAllEmployeesResponseDto;
import com.reliaquest.api.external.dto.GetEmployeeResponseDto;
import com.reliaquest.api.service.impl.EmployeeService;
import com.reliaquest.api.validator.EmployeeValidator;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeServiceIntegration employeeServiceIntegration;

    @Mock
    private EmployeeValidator employeeValidator;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void getAllEmployees_validCase_ReturnsListOfEmployees() {
        EmployeeDto e1 = new EmployeeDto();
        EmployeeDto e2 = new EmployeeDto();
        e1.setEmployeeName("akash");
        e2.setEmployeeName("saurabh");

        List<EmployeeDto> expectedEmployees = Arrays.asList(e1, e2);

        GetAllEmployeesResponseDto responseDto = new GetAllEmployeesResponseDto();
        responseDto.setData(expectedEmployees);

        when(employeeServiceIntegration.getAllEmployees()).thenReturn(responseDto);

        List<EmployeeDto> result = employeeService.getAllEmployees();

        assertEquals(expectedEmployees, result);
        verify(employeeServiceIntegration).getAllEmployees();
    }

    @Test
    void getEmployeeById_valid_ReturnsEmployeeData() {
        String id = UUID.randomUUID().toString();
        EmployeeDto expectedEmployee = new EmployeeDto();
        expectedEmployee.setEmployeeName("Saurabh");

        GetEmployeeResponseDto responseDto = new GetEmployeeResponseDto();
        responseDto.setData(expectedEmployee);

        when(employeeServiceIntegration.getEmployeeById(any(UUID.class))).thenReturn(responseDto);

        EmployeeDto result = employeeService.getEmployeeById(id);

        assertEquals(expectedEmployee, result);
        verify(employeeServiceIntegration).getEmployeeById(any(UUID.class));
    }

    @Test
    void getEmployeeById_InvalidUUID_ThrowsException() {
        String invalidId = "abcd";
        assertThrows(IllegalArgumentException.class, () -> employeeService.getEmployeeById(invalidId));
    }

    @Test
    void deleteEmployeeById_validCase_ReturnsSuccess() {
        String id = UUID.randomUUID().toString();
        EmployeeDto employee = new EmployeeDto();
        employee.setEmployeeName("Saurabh");

        DeleteEmployeeResponseDto responseDto = new DeleteEmployeeResponseDto();
        responseDto.setData(true);

        GetEmployeeResponseDto getEmployeeResponseDto = new GetEmployeeResponseDto();
        getEmployeeResponseDto.setData(employee);

        when(employeeServiceIntegration.getEmployeeById(any(UUID.class))).thenReturn(getEmployeeResponseDto);
        when(employeeServiceIntegration.deleteEmployeeByName(anyString())).thenReturn(responseDto);

        String result = employeeService.deleteEmployeeById(id);

        assertEquals("Employee deleted successfully", result);
        verify(employeeServiceIntegration).getEmployeeById(any(UUID.class));
        verify(employeeServiceIntegration).deleteEmployeeByName(anyString());
    }

    @Test
    void deleteEmployeeById_EmployeeNotFound_ThrowsEmployeeNotFoundException() {
        String id = UUID.randomUUID().toString();

        EmployeeDto employee = new EmployeeDto();
        employee.setEmployeeName("Saurabh");
        DeleteEmployeeResponseDto responseDto = new DeleteEmployeeResponseDto();
        responseDto.setData(false);

        GetEmployeeResponseDto getEmployeeResponseDto = new GetEmployeeResponseDto();
        getEmployeeResponseDto.setData(employee);

        when(employeeServiceIntegration.getEmployeeById(any(UUID.class))).thenReturn(getEmployeeResponseDto);
        when(employeeServiceIntegration.deleteEmployeeByName(anyString())).thenReturn(responseDto);

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployeeById(id));
    }

    @Test
    void createEmployee_validData_ReturnsCreatedEmployee() {
        CreateEmployeeRequestDto requestDto = new CreateEmployeeRequestDto();
        requestDto.setName("Saurabh");

        EmployeeDto expectedEmployee = new EmployeeDto();
        expectedEmployee.setEmployeeName("Saurabh");

        CreateEmployeeResponseDto responseDto = new CreateEmployeeResponseDto();
        responseDto.setData(expectedEmployee);

        when(employeeServiceIntegration.createEmployee(any(CreateEmployeeRequestDto.class)))
                .thenReturn(responseDto);

        EmployeeDto result = employeeService.createEmployee(requestDto);

        assertEquals(expectedEmployee, result);
        verify(employeeValidator).validateEmployeeData(requestDto);
        verify(employeeServiceIntegration).createEmployee(requestDto);
    }

    @Test
    void searchEmployeesByName_Success_ReturnsEmployees() {
        EmployeeDto e1 = new EmployeeDto();
        EmployeeDto e2 = new EmployeeDto();
        e1.setEmployeeName("akash");
        e2.setEmployeeName("saurabh");

        List<EmployeeDto> expectedEmployees = Arrays.asList(e1, e2);
        GetAllEmployeesResponseDto responseDto = new GetAllEmployeesResponseDto();
        responseDto.setData(expectedEmployees);

        when(employeeServiceIntegration.getAllEmployees()).thenReturn(responseDto);

        List<EmployeeDto> result = employeeService.searchEmployeesByName("saurabh");

        assertEquals(1, result.size());
    }

    @Test
    void getHighestSalaryOfEmployees_Success_Returns() {
        EmployeeDto e1 = new EmployeeDto();
        EmployeeDto e2 = new EmployeeDto();
        e1.setEmployeeName("akash");
        e1.setEmployeeSalary(1234);
        e2.setEmployeeName("saurabh");
        e2.setEmployeeSalary(9876);

        List<EmployeeDto> expectedEmployees = Arrays.asList(e1, e2);

        GetAllEmployeesResponseDto responseDto = new GetAllEmployeesResponseDto();
        responseDto.setData(expectedEmployees);

        when(employeeServiceIntegration.getAllEmployees()).thenReturn(responseDto);

        int result = employeeService.getHighestSalaryOfEmployees();

        assertEquals(9876, result);
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_Success() {
        EmployeeDto e1 = new EmployeeDto();
        EmployeeDto e2 = new EmployeeDto();
        e1.setEmployeeName("akash");
        e1.setEmployeeSalary(1234);
        e2.setEmployeeName("saurabh");
        e2.setEmployeeSalary(9876);

        List<EmployeeDto> expectedEmployees = Arrays.asList(e1, e2);
        GetAllEmployeesResponseDto responseDto = new GetAllEmployeesResponseDto();
        responseDto.setData(expectedEmployees);

        when(employeeServiceIntegration.getAllEmployees()).thenReturn(responseDto);

        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

        assertEquals(2, result.size());
    }
}
