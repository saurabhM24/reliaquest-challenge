package com.reliaquest.api.external.dto;

import com.reliaquest.api.dto.EmployeeDto;
import lombok.Data;

/**
 * Base class
 *
 * @author Saurabh
 */
@Data
public class EmployeeResponseDto {

    private EmployeeDto data;

    private String status;
}
