package com.whl.spring.demo.dao;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.entity.UserEntity;

@Mapper
public interface UserDao {
    Page<UserEntity> list(Page<?> page) throws Exception;

    UserEntity getById(Long id) throws Exception;

    int create(UserEntity demo) throws Exception;

    int update(UserEntity demo) throws Exception;

    int deleteById(Long id) throws Exception;
}
