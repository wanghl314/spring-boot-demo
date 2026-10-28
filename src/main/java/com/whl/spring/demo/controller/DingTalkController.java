package com.whl.spring.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DingTalkController {

    @PostMapping(value = "/topapi/v2/department/listsub", produces = MediaType.APPLICATION_JSON_VALUE)
    public String departmentListsub() {
        return "{\"errcode\":333,\"errmsg\":\"departmentListsub\"}";
    }

    @PostMapping(value = "/v1.0/todo/users/{unionId}/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    public String todoCreate(@PathVariable String unionId) {
        return "{\"errcode\":22,\"errmsg\":\"todoCreate\"}";
    }

}
