package com.reliaquest.api.service.impl;

import com.reliaquest.api.dto.CreateEmployeeRequestDto;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.external.EmployeeServiceIntegration;
import com.reliaquest.api.external.dto.CreateEmployeeResponseDto;
import com.reliaquest.api.external.dto.DeleteEmployeeResponseDto;
import com.reliaquest.api.external.dto.GetAllEmployeesResponseDto;
import com.reliaquest.api.external.dto.GetEmployeeResponseDto;
import com.reliaquest.api.service.IEmployeeService;
import com.reliaquest.api.validator.EmployeeValidator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {

    private final EmployeeServiceIntegration employeeServiceIntegration;

    private final EmployeeValidator employeeValidator;

    /**
     * Method to get the list of Employees
     *
     * @return {@link EmployeeDto} Return list of Employees
     */
    @Override
    public List<EmployeeDto> getAllEmployees() {
        GetAllEmployeesResponseDto getAllEmployeesResponseDto = employeeServiceIntegration.getAllEmployees();
        return getAllEmployeesResponseDto.getData();
    }

    /**
     * Method to get specific employee details
     *
     * @param id UUID of the employee
     * @return {@link EmployeeDto}
     */
    @Override
    public EmployeeDto getEmployeeById(String id) {
        UUID uuid = getUUID(id);

        GetEmployeeResponseDto getEmployeeResponseDto = employeeServiceIntegration.getEmployeeById(uuid);
        return getEmployeeResponseDto.getData();
    }

    /**
     * Method to delete employee by id
     *
     * @param id id of the employee to be deleted
     * @return Returns String
     */
    @Override
    public String deleteEmployeeById(String id) {
        EmployeeDto employeeDto = getEmployeeById(id);
        DeleteEmployeeResponseDto deleteEmployeeResponseDto =
                employeeServiceIntegration.deleteEmployeeByName(employeeDto.getEmployeeName());

        if (!deleteEmployeeResponseDto.getData()) {
            throw new EmployeeNotFoundException("Employee with id: " + id + " not found");
        }

        return "Employee deleted successfully";
    }

    /**
     * Method to create the employee
     *
     * @param createEmployeeRequestDto
     * @return {@link EmployeeDto}
     */
    @Override
    public EmployeeDto createEmployee(CreateEmployeeRequestDto createEmployeeRequestDto) {
        employeeValidator.validateEmployeeData(createEmployeeRequestDto);

        CreateEmployeeResponseDto createEmployeeResponseDto =
                employeeServiceIntegration.createEmployee(createEmployeeRequestDto);

        return createEmployeeResponseDto.getData();
    }

    /**
     * Method to search employees containing given name
     *
     * @param name Name of employee
     * @return {@link EmployeeDto}
     */
    @Override
    public List<EmployeeDto> searchEmployeesByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Search string(name) cannot be empty");
        }

        List<EmployeeDto> employeeDtoList = getAllEmployees();

        return employeeDtoList.stream()
                .filter(employeeDto ->
                        employeeDto.getEmployeeName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    /**
     * Method to get the highest salary
     *
     * @return Integer salary
     */
    @Override
    public int getHighestSalaryOfEmployees() {
        List<EmployeeDto> employeeDtoList = getAllEmployees();

        OptionalInt maxSalary = employeeDtoList.stream()
                .mapToInt(EmployeeDto::getEmployeeSalary)
                .max();

        if (maxSalary.isPresent()) return maxSalary.getAsInt();
        else throw new EmployeeNotFoundException("No employees found with highest salary");
    }

    /**
     * Method to get the top 10 salaried employees
     *
     * @return List of Employee Names
     */
    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        List<EmployeeDto> employeeDtoList = getAllEmployees();

        employeeDtoList.sort(Comparator.comparingInt(EmployeeDto::getEmployeeSalary));
        Collections.reverse(employeeDtoList);

        return employeeDtoList.stream()
                .limit(10)
                .map(EmployeeDto::getEmployeeName)
                .toList();
    }

    private UUID getUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid employee id : " + id + ", Requires employee id in UUID format.");
        }
    }
}
