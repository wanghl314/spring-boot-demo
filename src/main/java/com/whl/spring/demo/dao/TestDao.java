package com.whl.spring.demo.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.entity.TestEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestDao {
    Page<TestEntity> list(Page<?> page) throws Exception;

    TestEntity getById(Long id) throws Exception;

    int create(TestEntity test) throws Exception;

    int update(TestEntity test) throws Exception;

    int deleteById(Long id) throws Exception;
}
