package com.whl.spring.demo.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.CredentialsProviderBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(HttpClientProperties.class)
public class RestClientConfig {
    private static final int MAX_CONN_TOTAL = 2000;

    private static final int MAX_CONN_PER_ROUTE = 100;

    private static final Duration CONNECTION_REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);

    private static final Duration SOCKET_TIMEOUT = Duration.ofSeconds(20);

    @Bean
    public HttpClientBuilder httpClientBuilder(HttpClientProperties properties) {
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(CONNECT_TIMEOUT.getSeconds()))
                .build();
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(SOCKET_TIMEOUT.getSeconds()))
                .build();
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(MAX_CONN_TOTAL)
                .setMaxConnPerRoute(MAX_CONN_PER_ROUTE)
                .setDefaultConnectionConfig(connectionConfig)
                .setDefaultSocketConfig(socketConfig)
                .build();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(CONNECTION_REQUEST_TIMEOUT.getSeconds()))
                .build();
        HttpClientBuilder builder = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig);
        String host = properties.getProxy().getHost();
        int port = properties.getProxy().getPort();

        if (StringUtils.isNotBlank(host) && port > 0) {
            HttpHost proxy = new HttpHost(properties.getProxy().getScheme(), host, port);
            builder.setProxy(proxy);
            String username = properties.getProxy().getUsername();
            String password = properties.getProxy().getPassword();

            if (StringUtils.isNotBlank(username)) {
                CredentialsProvider credsProvider = CredentialsProviderBuilder.create()
                        .add(new AuthScope(host, port), username, password == null ? null : password.toCharArray())
                        .build();
                builder.setDefaultCredentialsProvider(credsProvider);
            }
        }
        return builder;
    }

    @Bean
    public ClientHttpRequestFactory requestFactory(HttpClientBuilder httpClientBuilder) {
        CloseableHttpClient httpClient = httpClientBuilder.build();
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    public RestClient.Builder restClientBuilder(ClientHttpRequestFactory requestFactory) {
        return RestClient.builder().requestFactory(requestFactory);
    }

}
