package com.reliaquest.api.dto;

import lombok.Data;

/**
 * This class represents the request body of create employee api.
 *
 * @author Saurabh
 */
@Data
public class CreateEmployeeRequestDto {

    private String name;

    private Integer salary;

    private Integer age;

    private String title;
}
