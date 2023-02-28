package com.whl.spring.demo.service;

import java.util.List;

import com.whl.spring.demo.entity.UserEntity;

public interface UserService {
    List<UserEntity> list() throws Exception;

    UserEntity getById(Long id) throws Exception;

    int create(UserEntity demo) throws Exception;

    int update(UserEntity demo) throws Exception;

    int deleteById(Long id) throws Exception;
}
