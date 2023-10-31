package com.whl.spring.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/routes")
public class RouteController {

    @GetMapping({"", "/"})
    public Map<Integer, String> index() {
        Map<Integer, String> routes = new HashMap<>();
        routes.put(1, "http://127.0.0.1:8080");
        routes.put(2, "http://192.168.1.241:8083");
        routes.put(3, "https://www.weaver.com.cn");
        return routes;
    }

}
