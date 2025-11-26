package com.whl.spring.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.entity.UserEntity;

import java.util.Map;

public interface UserService {
    Page<UserEntity> list(Page<?> page) throws Exception;

    UserEntity getById(Long id) throws Exception;

    int create(UserEntity user) throws Exception;

    int update(UserEntity user) throws Exception;

    int deleteById(Long id) throws Exception;

    Map<String, Object> selectAsMap() throws Exception;
}
