package com.logni.account.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerDocumentationConfig {


    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Logni Digital Bank Account Service")
                        .description("This is a Logni Digital bank's Account component. ")
                        .termsOfService("")
                        .version("1.0.0")
                        .license(new License()
                                .name("")
                                .url("http://logni.bank"))
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .email("logni@gmail.com")));
    }
}
