package com.psy.psychocenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Psychocenter API")
                        .version("1.0.0")
                        .description("API para gerenciamento de pacientes, supervisões e pagamentos")
                        .contact(new Contact()
                                .name("Marco Lobo")
                                .email("marcoantoniolobo82@gmail.com")
                                .url("https://github.com/marcolobo82"))
                );
    }
}
