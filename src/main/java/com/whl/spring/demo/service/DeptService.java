package com.whl.spring.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.entity.DeptEntity;

public interface DeptService {
    Page<DeptEntity> list(Page<?> page) throws Exception;

    DeptEntity getFirst() throws Exception;

    DeptEntity getById(Long id) throws Exception;

    int create(DeptEntity dept) throws Exception;

    int update(DeptEntity dept) throws Exception;

    int deleteById(Long id) throws Exception;
}
