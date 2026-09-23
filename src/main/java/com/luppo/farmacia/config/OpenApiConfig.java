package com.luppo.farmacia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Comparador de Precios de Medicamentos — El Salvador")
                        .version("1.0.0")
                        .description("API REST para comparación de precios en farmacias salvadoreñas y referencia oficial de PVMP emitida por la Superintendencia de Regulación Sanitaria (SRS).")
                        .contact(new Contact().name("Luppo / Nevora Software").email("contacto@nevorasoftware.com")))
                .servers(List.of(
                        new Server().url("/").description("Default Server")
                ));
    }
}
