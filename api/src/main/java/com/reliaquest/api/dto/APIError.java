package com.reliaquest.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class to represent the error response of the application
 *
 * @author Saurabh
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class APIError {

    private String error;
}
