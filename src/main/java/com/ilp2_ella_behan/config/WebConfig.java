package com.ilp2_ella_behan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve anything under classpath:/static/ at its direct path
        registry
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}
