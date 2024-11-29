package com.reliaquest.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * All the Rest api related configurations
 *
 * @author Saurabh
 */
@Configuration
public class RestConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
