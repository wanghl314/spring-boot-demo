package com.whl.spring.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.entity.PGVectorDemoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PGVectorDemoService {
    Page<PGVectorDemoEntity> list(Page<?> page) throws Exception;

    PGVectorDemoEntity getById(Long id) throws Exception;

    int create(PGVectorDemoEntity demo) throws Exception;

    int update(PGVectorDemoEntity demo) throws Exception;

    int deleteById(Long id) throws Exception;

    List<PGVectorDemoEntity> searchSimilar(@Param("vector") String vector, @Param("limit") int limit) throws Exception;
}
