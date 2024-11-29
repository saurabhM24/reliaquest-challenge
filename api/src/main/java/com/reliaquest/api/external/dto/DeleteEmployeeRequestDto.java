package com.reliaquest.api.external.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Request dto of delete employee by name API.
 *
 * @author Saurabh
 */
@Data
@AllArgsConstructor
public class DeleteEmployeeRequestDto {

    private String name;
}
