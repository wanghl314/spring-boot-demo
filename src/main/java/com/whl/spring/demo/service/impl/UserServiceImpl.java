package com.whl.spring.demo.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.dao.UserDao;
import com.whl.spring.demo.entity.UserEntity;
import com.whl.spring.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public Page<UserEntity> list(Page<?> page) throws Exception {
        return this.userDao.list(page);
    }

    @Override
    public UserEntity getById(Long id) throws Exception {
        return this.userDao.getById(id);
    }

    @Override
    @Transactional
    public int create(UserEntity user) throws Exception {
        return this.userDao.create(user);
    }

    @Override
    @Transactional
    public int update(UserEntity user) throws Exception {
        return this.userDao.update(user);
    }

    @Override
    @Transactional
    public int deleteById(Long id) throws Exception {
        return this.userDao.deleteById(id);
    }

}
