package com.whl.spring.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Default/boc_http_adp_gyzx/0.1/adapterAllHttp")
public class ESBController {
    @Autowired
    private ObjectMapper mapper;

    @RequestMapping(
            method = { RequestMethod.GET, RequestMethod.POST },
            value = {
                    "/37000001", "/37000002",
                    "/37000003", "/37000004",
                    "/37000005", "/37000006",
                    "/37000007", "/37000008",
                    "/37000009", "/37000010"
            })
    public String inter37000001(HttpServletRequest request) throws Exception {
        return this.response(request);
    }

    private String response(HttpServletRequest request) throws IOException {
        String header = request.getHeader("Individuation-Function-ID");
        String body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        String response = "header: " + header + ",body: " + body;
        Map<String, Object> json = new HashMap<>();
        json.put("errcode", 0);
        json.put("errmsg", "ok");
        json.put("access_token", response);
        json.put("expires_in", 7200);
        Map<String, String> responseString = new HashMap<>();
        responseString.put("responseString", mapper.writeValueAsString(json));
        List<Map<String, String>> list = new ArrayList<>();
        list.add(responseString);
        Map<String, List<Map<String, String>>> data = new HashMap<>();
        data.put("data", list);
        String jsonString = mapper.writeValueAsString(data);
        System.out.println(jsonString);
        return jsonString;
    }

}
