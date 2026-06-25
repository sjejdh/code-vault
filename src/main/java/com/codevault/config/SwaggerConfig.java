package com.codevault.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 接口文档配置
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CodeVault API")
                        .description("在线代码片段管理系统 - RESTful API 文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Hou Xingke")
                                .email("15839521565@163.com"))
                        .license(new License()
                                .name("MIT License")))
                // 添加全局安全要求：所有接口默认需要 Bearer Token
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                // 定义安全方案：Bearer Token (JWT)
                .schemaRequirement(SECURITY_SCHEME_NAME, new SecurityScheme()
                        .name(SECURITY_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
    }
}
