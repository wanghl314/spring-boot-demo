package com.whl.spring.demo.controller;

import com.whl.spring.demo.entity.TestEntity;
import com.whl.spring.demo.service.TestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestService testService;

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

    @GetMapping("/oracle")
    public TestEntity test() throws Exception {
        TestEntity entity = new TestEntity("啊".repeat(100000));
        this.testService.create(entity);
        return this.testService.getById(entity.getId());
    }

}
