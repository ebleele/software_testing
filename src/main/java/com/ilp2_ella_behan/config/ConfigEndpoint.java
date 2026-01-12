package com.ilp2_ella_behan.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfigEndpoint {
    private static final String DEFAULT_ILP_ENDPOINT =
            "https://ilp-rest-2025-bvh6e9hschfagrgy.ukwest-01.azurewebsites.net/";
    @Bean
    public String ilpEndpoint(){
        return System.getenv().getOrDefault("ILP_ENDPOINT", DEFAULT_ILP_ENDPOINT);
    }
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
