package com.whl.spring.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.entity.TestEntity;

public interface TestService {
    Page<TestEntity> list(Page<?> page) throws Exception;

    TestEntity getById(Long id) throws Exception;

    int create(TestEntity test) throws Exception;

    int update(TestEntity test) throws Exception;

    int deleteById(Long id) throws Exception;
}
