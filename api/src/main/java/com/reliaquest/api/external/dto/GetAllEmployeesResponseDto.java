package com.reliaquest.api.external.dto;

import com.reliaquest.api.dto.EmployeeDto;
import java.util.List;
import lombok.Data;

/**
 * This class represents response of Get all employees API.
 *
 * @author Saurabh
 */
@Data
public class GetAllEmployeesResponseDto {

    private List<EmployeeDto> data;

    private String status;
}
