package com.psy.psychocenter.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@Profile("dev")
public class OpenApiConfig {

    @Bean
    OpenAPI psychocenterOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("🧠 PsyCenter API")
                .description("""
                    API REST para gerenciamento de pacientes e supervisões.

                    Desenvolvido com Spring Boot 3, JPA, PostgreSQL e Spring Security (JWT).
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Marco Lobo")
                    .email("marcoantoniolobo82@gmail.com")
                    .url("https://github.com/MarcoAntonioLobo/psychocenter.git"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT"))
            )
            .addServersItem(new Server().url("http://localhost:8080").description("🔹 Ambiente Local"))
            .externalDocs(new ExternalDocumentation()
                .description("Documentação completa do projeto")
                .url("https://github.com/MarcoAntonioLobo/psychocenter.git"));
    }

    @Bean
    GroupedOpenApi patientsGroup() {
        return GroupedOpenApi.builder().group("👥 Pacientes").pathsToMatch("/patients/**").build();
    }

    @Bean
    GroupedOpenApi appointmentsGroup() {
        return GroupedOpenApi.builder().group("📅 Agendamentos").pathsToMatch("/appointments/**").build();
    }

    @Bean
    GroupedOpenApi paymentsGroup() {
        return GroupedOpenApi.builder().group("💳 Pagamentos").pathsToMatch("/payments/**").build();
    }

    @Bean
    GroupedOpenApi supervisionsGroup() {
        return GroupedOpenApi.builder().group("🧩 Supervisões").pathsToMatch("/supervisions/**").build();
    }

    @Bean
    GroupedOpenApi groupsGroup() {
        return GroupedOpenApi.builder().group("👪 Grupos").pathsToMatch("/groups/**").build();
    }
}
