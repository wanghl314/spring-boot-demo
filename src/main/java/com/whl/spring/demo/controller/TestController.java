package com.whl.spring.demo.controller;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping({"", "/"})
    public Map<String, Map<String, ?>> index(HttpServletRequest request, @RequestBody Map<String, ?> vo) throws Exception {
        Map<String, List<String>> headerMap = new HashMap<String, List<String>>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = request.getHeaders(headerName);
            List<String> values = Collections.list(headerValues);
            headerMap.put(headerName, values);
        }
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Map<String, ?>> result = new HashMap<String, Map<String, ?>>();
        result.put("header", headerMap);
        result.put("request", requestMap);
        result.put("body", vo);
        return result;
    }

}
