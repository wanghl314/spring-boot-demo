package com.whl.spring.demo.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.dao.TestDao;
import com.whl.spring.demo.entity.TestEntity;
import com.whl.spring.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private TestDao testDao;

    @Autowired
    private Snowflake snowflake;

    @Override
    public Page<TestEntity> list(Page<?> page) throws Exception {
        return this.testDao.list(page);
    }

    @Override
    public TestEntity getById(Long id) throws Exception {
        return this.testDao.getById(id);
    }

    @Override
    @Transactional
    public int create(TestEntity test) throws Exception {
        if (test.getId() == null) {
            test.setId(this.snowflake.nextId());
        }
        return this.testDao.create(test);
    }

    @Override
    @Transactional
    public int update(TestEntity test) throws Exception {
        return this.testDao.update(test);
    }

    @Override
    @Transactional
    public int deleteById(Long id) throws Exception {
        return this.testDao.deleteById(id);
    }

}
