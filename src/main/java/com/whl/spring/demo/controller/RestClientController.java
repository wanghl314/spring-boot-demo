package com.whl.spring.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/restclient")
public class RestClientController {
    private static Logger logger = LoggerFactory.getLogger(RestClientController.class);

    private final RestClient restClient;

    private PoolingHttpClientConnectionManager connManager;

    private Field connManagerClosedField;

    public RestClientController(RestClient.Builder builder) {
        this.restClient = builder.build();
        try {
            Field field = builder.getClass().getDeclaredField("requestFactory");
            field.setAccessible(true);
            ClientHttpRequestFactory requestFactory = (ClientHttpRequestFactory) field.get(builder);

            if (requestFactory instanceof HttpComponentsClientHttpRequestFactory) {
                field = requestFactory.getClass().getDeclaredField("httpClient");
                field.setAccessible(true);
                HttpClient httpClient = (HttpClient) field.get(requestFactory);

                if (httpClient != null) {
                    field = httpClient.getClass().getDeclaredField("connManager");
                    field.setAccessible(true);
                    HttpClientConnectionManager connManager = (HttpClientConnectionManager) field.get(httpClient);

                    if (connManager instanceof PoolingHttpClientConnectionManager poolingConnManager) {
                        this.connManager = poolingConnManager;
                        field = this.connManager.getClass().getDeclaredField("closed");
                        field.setAccessible(true);
                        this.connManagerClosedField = field;
                    }
                }
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }

    @GetMapping("")
    public String index() {
        return this.restClient.toString();
    }

    @GetMapping("/stats")
    public String stats() {
        if (this.connManager != null) {
            boolean alreadyClosed = false;

            if (this.connManagerClosedField != null) {
                AtomicBoolean closed = null;

                try {
                    closed = (AtomicBoolean) this.connManagerClosedField.get(this.connManager);
                } catch (Throwable t) {
                    logger.error(t.getMessage(), t);
                }

                if (closed != null) {
                    alreadyClosed = closed.get();
                }
            }

            if (alreadyClosed) {
                return "Connection pool shut down";
            }
            Set<HttpRoute> routes = this.connManager.getRoutes();

            if (CollectionUtils.isEmpty(routes)) {
                return this.connManager.getTotalStats().toString();
            }
            StringBuilder buf = new StringBuilder();

            for (HttpRoute route : routes) {
                buf.append(route.getTargetHost());
                buf.append(" -> ");
                buf.append(this.connManager.getStats(route).toString());
                buf.append("<br/>");
            }
            return buf.toString();
        }
        return "no stats infos";
    }

    @GetMapping("/close")
    public String close() {
        if (this.connManager != null) {
            this.connManager.close();
        }
        return "closed";
    }

    @RequestMapping("/proxy")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request, @RequestParam String url) {
        HttpMethod method = HttpMethod.valueOf(StringUtils.upperCase(request.getMethod()));
        Map<String, List<String>> requestHeaders = new HashMap<>();
        Iterator<String> it = request.getHeaderNames().asIterator();

        while (it.hasNext()) {
            String headerName = it.next();

            if (!Strings.CI.equalsAny(headerName, HttpHeaders.HOST)) {
                List<String> headerValues = requestHeaders.get(headerName);

                if (headerValues == null) {
                    headerValues = new ArrayList<>();
                }
                Iterator<String> innerIt = request.getHeaders(headerName).asIterator();

                while (innerIt.hasNext()) {
                    String headerValue = innerIt.next();
                    headerValues.add(headerValue);
                }
                requestHeaders.put(headerName, headerValues);
            }
        }

        return this.restClient
                .method(method)
                .uri(url)
                .headers(httpHeaders -> httpHeaders.putAll(requestHeaders))
                .retrieve()
                .toEntity(byte[].class);
    }

}
