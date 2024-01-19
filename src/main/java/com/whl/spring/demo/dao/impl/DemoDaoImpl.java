package com.whl.spring.demo.dao.impl;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.whl.spring.demo.dao.DemoDao;
import com.whl.spring.demo.entity.DemoEntity;

@Repository
public class DemoDaoImpl implements DemoDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<DemoEntity> list() throws Exception {
        return this.mongoTemplate.find(new Query().with(Sort.by(Sort.Order.asc("id"))), DemoEntity.class);
    }

    @Override
    public DemoEntity getById(Long id) {
        return this.mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), DemoEntity.class);
    }

    @Override
    public int create(DemoEntity demo) {
        if (demo.getId() == null || demo.getId() <= 0) {
            demo.setId(RandomUtils.nextLong());
        }
        this.mongoTemplate.insert(demo);
        return 1;
    }

    @Override
    public int update(DemoEntity demo) {
        Query query = new Query(Criteria.where("id").is(demo.getId()));
        Update update = new Update();

        if (StringUtils.isNotBlank(demo.getTitle())) {
            update.set("title", demo.getTitle());
        }

        if (StringUtils.isNotBlank(demo.getContent())) {
            update.set("content", demo.getContent());
        }
        UpdateResult result = this.mongoTemplate.updateFirst(query, update, DemoEntity.class);
        return (int) result.getModifiedCount();
    }

    @Override
    public int deleteById(Long id) {
        DeleteResult result = this.mongoTemplate.remove(new Query(Criteria.where("id").is(id)), DemoEntity.class);
        return (int) result.getDeletedCount();
    }

}
