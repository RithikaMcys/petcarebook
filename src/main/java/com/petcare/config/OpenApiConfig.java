package com.petcare.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI petCareOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PetCareBook API - Automated Pet Vaccination & Deworming Tracker")
                        .version("1.0.0")
                        .description("Backend REST APIs for storing pet vaccination histories, enforcing non-future immunization dates, auto-calculating next due dates, and alerting on due-soon vaccines.")
                        .contact(new Contact().name("PetCareBook Engineering").email("support@petcarebook.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
