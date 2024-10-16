package com.whl.spring.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.*;

@RestController
@RequestMapping("/restclient")
public class RestClientController {
    private final RestClient restClient;

    public RestClientController(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @GetMapping("")
    public String index() {
        return this.restClient.toString();
    }

    @RequestMapping("/proxy")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request, @RequestParam String url) {
        HttpMethod method = HttpMethod.valueOf(StringUtils.upperCase(request.getMethod()));
        Map<String, List<String>> requestHeaders = new HashMap<>();
        Iterator<String> it = request.getHeaderNames().asIterator();

        while (it.hasNext()) {
            String headerName = it.next();

            if (!StringUtils.equalsAnyIgnoreCase(headerName, HttpHeaders.HOST)) {
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
