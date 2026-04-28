package com.whl.spring.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.bean.PGVectorDemoQuery;
import com.whl.spring.demo.entity.PGVectorDemoEntity;
import com.whl.spring.demo.service.PGVectorDemoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 功能仅限当连接 postgresql 数据库时可用
 */
@RestController
@RequestMapping("/pgvector")
public class PGVectorDemoController {
    @Autowired
    private PGVectorDemoService demoService;

    @GetMapping(value = {"", "/"})
    public String index() {
        return "PGVectorDemo";
    }

    @PostMapping(value = "/list")
    public Page<PGVectorDemoEntity> list(@RequestBody @Validated PGVectorDemoQuery query) throws Exception {
        return this.demoService.list(new Page<PGVectorDemoEntity>(query.getPageNo(), query.getPageSize()));
    }

    @GetMapping("/getById/{id}")
    public PGVectorDemoEntity getById(@PathVariable Long id) throws Exception {
        return this.demoService.getById(id);
    }

    @PostMapping("/save")
    public PGVectorDemoEntity save(@RequestBody PGVectorDemoEntity entity) throws Exception {
        int result;

        if (entity.getId() == null) {
            result = this.demoService.create(entity);
        } else {
            result = this.demoService.update(entity);
        }

        if (result > 0) {
            return entity;
        }
        return null;
    }

    @GetMapping("/deleteById/{id}")
    public int deleteById(@PathVariable Long id) throws Exception {
        return this.demoService.deleteById(id);
    }

    @PostMapping("/searchSimilar")
    public List<PGVectorDemoEntity> searchSimilar(@RequestBody Map<String, String> search) throws Exception {
        String vector = search.get("vector");
        String limit = search.get("limit");
        int limitInt = 5;

        if (StringUtils.isNotBlank(limit)) {
            try {
                limitInt = Integer.parseInt(limit);
            } catch (NumberFormatException ignore) {
            }
        }
        return this.demoService.searchSimilar(vector, limitInt);
    }

}
