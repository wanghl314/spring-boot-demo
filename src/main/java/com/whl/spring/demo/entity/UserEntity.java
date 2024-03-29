package com.whl.spring.demo.entity;

import java.io.Serial;
import java.io.Serializable;

import org.springframework.beans.BeanUtils;

import com.whl.spring.demo.dto.UserDto;

public class UserEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 255122426203916350L;

    private Long id;

    private String username;

    private int age;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public UserDto toDto() {
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }

}
