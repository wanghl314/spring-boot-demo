package com.whl.spring.demo.dto;

import java.io.Serial;
import java.io.Serializable;

public class UserDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -7156122727717081907L;

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

}
