package com.whl.spring.demo.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.dao.PGVectorDemoDao;
import com.whl.spring.demo.entity.PGVectorDemoEntity;
import com.whl.spring.demo.service.PGVectorDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PGVectorDemoServiceImpl implements PGVectorDemoService {
    @Autowired
    private PGVectorDemoDao demoDao;

    @Override
    public Page<PGVectorDemoEntity> list(Page<?> page) throws Exception {
        return this.demoDao.list(page);
    }

    @Override
    public PGVectorDemoEntity getById(Long id) throws Exception {
        return this.demoDao.getById(id);
    }

    @Override
    @Transactional
    public int create(PGVectorDemoEntity demo) throws Exception {
        return this.demoDao.create(demo);
    }

    @Override
    @Transactional
    public int update(PGVectorDemoEntity demo) throws Exception {
        return this.demoDao.update(demo);
    }

    @Override
    @Transactional
    public int deleteById(Long id) throws Exception {
        return this.demoDao.deleteById(id);
    }

    @Override
    public List<PGVectorDemoEntity> searchSimilar(String vector, int limit) throws Exception {
        return this.demoDao.searchSimilar(vector, limit);
    }

}
