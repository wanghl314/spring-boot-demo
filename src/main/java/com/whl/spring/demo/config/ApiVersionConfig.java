package com.whl.spring.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer.usePathSegment(1, path -> path.pathWithinApplication().value().startsWith("/api/"))
                .setVersionRequired(false)
                .setDefaultVersion("1")
                .addSupportedVersions("1", "1.1", "1.2", "1.3", "2");
    }

}
