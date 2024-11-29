package com.reliaquest.api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {

    @Value("${employee.base-url}")
    private String employeeServiceBaseUrl;

    @Value("${employee.resource-url}")
    private String employeeServiceResourceUrl;
}
