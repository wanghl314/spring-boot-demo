package com.whl.spring.demo.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    Page<UserEntity> list(Page<?> page) throws Exception;

    UserEntity getById(Long id) throws Exception;

    int create(UserEntity demo) throws Exception;

    int update(UserEntity demo) throws Exception;

    int deleteById(Long id) throws Exception;
}
