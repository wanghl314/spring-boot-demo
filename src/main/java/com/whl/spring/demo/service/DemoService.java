package com.whl.spring.demo.service;

import java.util.List;

import com.whl.spring.demo.entity.DemoEntity;

public interface DemoService {
    List<DemoEntity> list() throws Exception;

    DemoEntity getById(Long id) throws Exception;

    int create(DemoEntity demo) throws Exception;

    int update(DemoEntity demo) throws Exception;

    int deleteById(Long id) throws Exception;
}
