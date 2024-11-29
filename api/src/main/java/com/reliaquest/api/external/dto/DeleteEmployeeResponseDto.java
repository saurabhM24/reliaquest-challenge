package com.reliaquest.api.external.dto;

import lombok.Data;

/**
 * This Dto class represents the response of Delete Employee by name api.
 *
 * @author Saurabh
 */
@Data
public class DeleteEmployeeResponseDto {

    private Boolean data;

    private String status;
}
