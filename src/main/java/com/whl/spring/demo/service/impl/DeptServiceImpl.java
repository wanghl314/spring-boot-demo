package com.whl.spring.demo.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.dao.DeptDao;
import com.whl.spring.demo.entity.DeptEntity;
import com.whl.spring.demo.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeptServiceImpl implements DeptService {
    @Autowired
    private DeptDao deptDao;

    @Autowired
    private Snowflake snowflake;

    @Override
    public Page<DeptEntity> list(Page<?> page) throws Exception {
        return this.deptDao.list(page);
    }

    @Override
    public DeptEntity getFirst() throws Exception {
        return this.deptDao.getFirst();
    }

    @Override
    public DeptEntity getById(Long id) throws Exception {
        return this.deptDao.getById(id);
    }

    @Override
    @Transactional
    public int create(DeptEntity dept) throws Exception {
        if (dept.getId() == null) {
            dept.setId(this.snowflake.nextId());
        }
        return this.deptDao.create(dept);
    }

    @Override
    @Transactional
    public int update(DeptEntity dept) throws Exception {
        return this.deptDao.update(dept);
    }

    @Override
    @Transactional
    public int deleteById(Long id) throws Exception {
        return this.deptDao.deleteById(id);
    }

}
