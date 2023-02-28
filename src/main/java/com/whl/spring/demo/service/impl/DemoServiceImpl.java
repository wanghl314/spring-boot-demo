package com.whl.spring.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.whl.spring.demo.dao.DemoDao;
import com.whl.spring.demo.entity.DemoEntity;
import com.whl.spring.demo.service.DemoService;

@Service
public class DemoServiceImpl implements DemoService {
    @Autowired
    private DemoDao demoDao;

    @Override
    public List<DemoEntity> list() throws Exception {
        return this.demoDao.list();
    }

    @Override
    public DemoEntity getById(Long id) throws Exception {
        return this.demoDao.getById(id);
    }

    @Override
    @Transactional
    public int create(DemoEntity demo) throws Exception {
        return this.demoDao.create(demo);
    }

    @Override
    @Transactional
    public int update(DemoEntity demo) throws Exception {
        return this.demoDao.update(demo);
    }

    @Override
    @Transactional
    public int deleteById(Long id) throws Exception {
        return this.demoDao.deleteById(id);
    }

}
