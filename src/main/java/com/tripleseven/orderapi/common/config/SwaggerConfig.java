package com.tripleseven.orderapi.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        Info info = new Info()
                .title("bookstore 프로젝트 ORDER-API DOCUMENT")
                .description("bookstore 프로젝트의 API 명세서입니다.");

                return new OpenAPI()
                        .components(new Components())
                        .info(info);
    }
}
