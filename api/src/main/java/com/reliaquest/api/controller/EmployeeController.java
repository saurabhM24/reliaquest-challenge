package com.reliaquest.api.controller;

import com.reliaquest.api.dto.CreateEmployeeRequestDto;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.service.IEmployeeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for Employee related APIs
 *
 * @author Saurabh
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<EmployeeDto, CreateEmployeeRequestDto> {

    private final IEmployeeService employeeService;

    @Override
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        log.info("Received API request to get All employees");

        List<EmployeeDto> employeeDtoList = employeeService.getAllEmployees();
        return ResponseEntity.status(HttpStatus.OK).body(employeeDtoList);
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByNameSearch(
            @PathVariable("searchString") String searchString) {
        log.info("Received API request to search employees by name: {}", searchString);

        List<EmployeeDto> employeeDtoList = employeeService.searchEmployeesByName(searchString);
        return ResponseEntity.status(HttpStatus.OK).body(employeeDtoList);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable("id") String id) {
        log.info("Received API request to get employee by id: {}", id);

        EmployeeDto employeeDto = employeeService.getEmployeeById(id);
        return ResponseEntity.status(HttpStatus.OK).body(employeeDto);
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("Received API request to get the highest salary of employee");

        Integer maxSalary = employeeService.getHighestSalaryOfEmployees();
        return ResponseEntity.status(HttpStatus.OK).body(maxSalary);
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("Received API request to get the Top 10 highest salaried employees");

        List<String> employeeNamesList = employeeService.getTopTenHighestEarningEmployeeNames();
        return ResponseEntity.status(HttpStatus.OK).body(employeeNamesList);
    }

    @Override
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody CreateEmployeeRequestDto employeeInput) {
        log.info("Received API request to create employee");

        EmployeeDto createdEmployee = employeeService.createEmployee(employeeInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") String id) {
        log.info("Received API request to delete employee by id: {}", id);

        String response = employeeService.deleteEmployeeById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
