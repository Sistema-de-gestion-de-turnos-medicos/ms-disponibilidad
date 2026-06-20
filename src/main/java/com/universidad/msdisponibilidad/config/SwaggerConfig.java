package com.universidad.msdisponibilidad.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración central de OpenAPI / Swagger UI para el microservicio de disponibilidad.
 * <p>
 * Define la metadata general de la documentación (título, versión, contacto).
 * Este microservicio no implementa seguridad propia, por lo que no se declara
 * ningún esquema de autenticación.
 * <p>
 * La documentación queda disponible en:
 * <ul>
 *     <li>JSON: {@code /v3/api-docs}</li>
 *     <li>UI:   {@code /doc/swagger-ui.html}</li>
 * </ul>
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server().url("http://localhost:8087").description("Servidor local")
                ));
    }

    private Info apiInfo() {
        return new Info()
                .title("MS Disponibilidad - API de Gestión de Disponibilidad Médica")
                .description("""
                        Microservicio encargado de gestionar la disponibilidad horaria
                        de los médicos dentro del Sistema de Gestión de Turnos Médicos.
                        Permite registrar, consultar, actualizar y eliminar bloques de
                        disponibilidad por médico y día de la semana.""")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Equipo Turnos Médicos")
                        .email("soporte@turnosmedicos.com"));
    }
}
