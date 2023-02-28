package com.whl.spring.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.whl.spring.demo.dao.UserDao;
import com.whl.spring.demo.entity.UserEntity;
import com.whl.spring.demo.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public List<UserEntity> list() throws Exception {
        return this.userDao.list();
    }

    @Override
    public UserEntity getById(Long id) throws Exception {
        return this.userDao.getById(id);
    }

    @Override
    @Transactional
    public int create(UserEntity demo) throws Exception {
        return this.userDao.create(demo);
    }

    @Override
    @Transactional
    public int update(UserEntity demo) throws Exception {
        return this.userDao.update(demo);
    }

    @Override
    @Transactional
    public int deleteById(Long id) throws Exception {
        return this.userDao.deleteById(id);
    }

}
