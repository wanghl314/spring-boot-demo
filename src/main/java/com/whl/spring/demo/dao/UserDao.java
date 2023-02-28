package com.whl.spring.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.whl.spring.demo.entity.UserEntity;

@Mapper
public interface UserDao {
    List<UserEntity> list() throws Exception;

    UserEntity getById(Long id) throws Exception;

    int create(UserEntity demo) throws Exception;

    int update(UserEntity demo) throws Exception;

    int deleteById(Long id) throws Exception;
}
