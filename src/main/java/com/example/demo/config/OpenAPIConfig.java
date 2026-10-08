package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI (biblioteca springdoc-openapi), vista na
 * aula sobre documentação de API. Define as informações gerais da API e o
 * esquema de segurança Bearer/JWT, usado pelo botão "Authorize" do
 * Swagger UI (disponível em {@code /swagger-ui.html} depois que o
 * SecurityConfig libera essa rota).
 */
@Configuration
public class OpenAPIConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("CarboFlix API")
                        .description("API REST do CarboFlix — catálogo de filmes com perfis, categorias e "
                                + "avaliações por conta. Projeto acadêmico da disciplina de backend (UNESC).")
                        .version("v1.0"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
