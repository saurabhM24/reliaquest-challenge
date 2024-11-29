package com.reliaquest.api.validator;

import com.reliaquest.api.dto.CreateEmployeeRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator class for Employee data
 */
@Component
@Slf4j
public class EmployeeValidator {

    public void validateEmployeeData(CreateEmployeeRequestDto createEmployeeRequestDto) {
        String name = createEmployeeRequestDto.getName();
        String title = createEmployeeRequestDto.getTitle();

        Integer salary = createEmployeeRequestDto.getSalary();
        Integer age = createEmployeeRequestDto.getAge();

        if (age == null || age < 16 || age > 75) {
            log.error("Invalid Employee age: {}", age);
            throw new IllegalArgumentException("Employee age must be between 16 and 75");
        }

        if (name == null || name.isEmpty()) {
            log.error("Invalid Employee name: {}", name);
            throw new IllegalArgumentException("Employee name must specified");
        }

        if (salary == null || salary <= 0) {
            log.error("Invalid Employee salary: {}", salary);
            throw new IllegalArgumentException("Employee salary must be greater than zero");
        }

        if (title == null || title.isEmpty()) {
            log.error("Invalid Employee title: {}", title);
            throw new IllegalArgumentException("Employee title must specified");
        }
    }
}
