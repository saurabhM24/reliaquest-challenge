package com.reliaquest.api.service;

import com.reliaquest.api.dto.CreateEmployeeRequestDto;
import com.reliaquest.api.dto.EmployeeDto;
import java.util.List;

/**
 * Business logic for Employee related APIs
 *
 * @author Saurabh
 */
public interface IEmployeeService {

    /**
     * Method to get the list of Employees
     *
     * @return {@link EmployeeDto} Return list of Employees
     */
    List<EmployeeDto> getAllEmployees();

    /**
     * Method to get specific employee details
     *
     * @param id UUID of the employee
     * @return {@link EmployeeDto}
     */
    EmployeeDto getEmployeeById(String id);

    /**
     * Method to delete employee by id
     *
     * @param id id of the employee to be deleted
     * @return Returns String
     */
    String deleteEmployeeById(String id);

    /**
     * Method to create the employee
     *
     * @param createEmployeeRequestDto
     * @return {@link EmployeeDto}
     */
    EmployeeDto createEmployee(CreateEmployeeRequestDto createEmployeeRequestDto);

    /**
     * Method to search employees containing given name
     *
     * @param name Name of employee
     * @return {@link EmployeeDto}
     */
    List<EmployeeDto> searchEmployeesByName(String name);

    /**
     * Method to get the highest salary
     *
     * @return Integer salary
     */
    int getHighestSalaryOfEmployees();

    /**
     * Method to get the top 10 salaried employees
     *
     * @return List of Employee Names
     */
    List<String> getTopTenHighestEarningEmployeeNames();
}
