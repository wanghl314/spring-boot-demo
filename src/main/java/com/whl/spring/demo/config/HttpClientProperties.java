package com.whl.spring.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = HttpClientProperties.PREFIX)
public class HttpClientProperties {
    public static final String PREFIX = "httpclient";

    private Proxy proxy = new Proxy();

    @Getter
    @Setter
    public static class Proxy {
        private String scheme = "http";

        private String host;

        private int port;

        private String username;

        private String password;
    }

}
