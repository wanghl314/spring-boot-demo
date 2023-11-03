package com.whl.spring.demo.vo;

import java.io.Serial;
import java.io.Serializable;

import org.springframework.beans.BeanUtils;

import com.whl.spring.demo.entity.UserEntity;

public class UserVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -9122414045682007309L;

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

    public UserEntity toEntity() {
        UserEntity entity = new UserEntity();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

}
