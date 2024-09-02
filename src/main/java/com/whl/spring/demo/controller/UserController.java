package com.whl.spring.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.bean.UserQuery;
import com.whl.spring.demo.dto.UserDto;
import com.whl.spring.demo.entity.UserEntity;
import com.whl.spring.demo.service.UserService;
import com.whl.spring.demo.vo.UserVo;
import jakarta.validation.Valid;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    @PostMapping(value = "/list")
    public Page<UserDto> list(@Valid @RequestBody UserQuery query) throws Exception {
        Page<UserDto> result = new Page<UserDto>();
        Page<UserEntity> data = this.userService.list(new Page<UserEntity>(query.getPageNo(), query.getPageSize()));

        if (data != null) {
            BeanUtils.copyProperties(data, result, "records");

            if (data.getRecords() != null) {
                result.setRecords(data.getRecords().stream().map(UserEntity::toDto).collect(Collectors.toList()));
            }
        }
        return result;
    }

    @GetMapping("/getById/{id}")
    public UserDto getById(@PathVariable Long id) throws Exception {
        UserEntity entity = this.userService.getById(id);
        UserDto result = null;

        if (entity != null) {
            result = entity.toDto();
        }
        return result;
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
