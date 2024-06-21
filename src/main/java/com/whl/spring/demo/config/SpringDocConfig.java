package com.whl.spring.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(this.getApiInfo());
    }
   
    private Info getApiInfo() {
        return new Info()
        .title("spring-boot-demo")// 配置文档标题
        .description("spring-boot-demo")// 配置文档描述
        .contact(new Contact().name("魑魅魍魉").url(""))// 配置作者信息
        .license(new License().name("Apache 2.0").url(""))// 配置License许可证信息
        .summary("spring-boot-demo")// 概述信息
        .termsOfService("")
        .version("1.0");// 配置版本号
    }

}
