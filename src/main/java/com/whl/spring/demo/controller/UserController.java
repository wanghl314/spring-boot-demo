package com.whl.spring.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whl.spring.demo.entity.UserEntity;
import com.whl.spring.demo.service.UserService;
import com.whl.spring.demo.vo.UserVo;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = {"", "/"})
    public String index() {
        return "User";
    }

    @GetMapping("/test")
    public Object test() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("a", RandomStringUtils.randomAlphabetic(10));
        data.put("b", RandomStringUtils.randomAlphanumeric(10));
        data.put("c", RandomStringUtils.randomAscii(10));
        data.put("d", RandomStringUtils.randomNumeric(10));
        return data;
    }

    @GetMapping("/list")
    public List<UserEntity> list() throws Exception {
        return this.userService.list();
    }

    @GetMapping("/getById/{id}")
    public UserEntity getById(@PathVariable Long id) throws Exception {
        return this.userService.getById(id);
    }

    @PostMapping("/save")
    public int save(@RequestBody UserVo vo) throws Exception {
        UserEntity entity = vo.toEntity();

        if (entity.getId() == null) {
            return this.userService.create(entity);
        } else {
            return this.userService.update(entity);
        }
    }

    @GetMapping("/deleteById/{id}")
    public int deleteById(@PathVariable Long id) throws Exception {
        return this.userService.deleteById(id);
    }

}
