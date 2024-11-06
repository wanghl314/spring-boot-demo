package com.whl.spring.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.bean.TestQuery;
import com.whl.spring.demo.dto.TestDto;
import com.whl.spring.demo.entity.TestEntity;
import com.whl.spring.demo.service.TestService;
import com.whl.spring.demo.vo.TestVo;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestService testService;

    @PostMapping({"", "/"})
    public Map<String, Map<String, ?>> index(HttpServletRequest request, @RequestBody Map<String, ?> vo) throws Exception {
        Map<String, List<String>> headerMap = new HashMap<String, List<String>>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = request.getHeaders(headerName);
            List<String> values = Collections.list(headerValues);
            headerMap.put(headerName, values);
        }
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Map<String, ?>> result = new HashMap<String, Map<String, ?>>();
        result.put("header", headerMap);
        result.put("request", requestMap);
        result.put("body", vo);
        return result;
    }

    @PostMapping(value = "/list")
    public Page<TestDto> list(@RequestBody @Validated TestQuery query) throws Exception {
        Page<TestDto> result = new Page<TestDto>();
        Page<TestEntity> data = this.testService.list(new Page<TestEntity>(query.getPageNo(), query.getPageSize()));

        if (data != null) {
            BeanUtils.copyProperties(data, result, "records");

            if (data.getRecords() != null) {
                result.setRecords(data.getRecords().stream().map(TestEntity::toDto).collect(Collectors.toList()));
            }
        }
        return result;
    }

    @GetMapping("/getById/{id}")
    public TestDto getById(@PathVariable Long id) throws Exception {
        TestEntity entity = this.testService.getById(id);
        TestDto result = null;

        if (entity != null) {
            result = entity.toDto();
        }
        return result;
    }

    @PostMapping("/save")
    public int save(@RequestBody TestVo vo) throws Exception {
        TestEntity entity = vo.toEntity();

        if (entity.getId() == null) {
            if (StringUtils.isNotBlank(entity.getTest())) {
                entity.setTest("啊".repeat(100000));
            }
            return this.testService.create(entity);
        } else {
            return this.testService.update(entity);
        }
    }

    @GetMapping("/deleteById/{id}")
    public int deleteById(@PathVariable Long id) throws Exception {
        return this.testService.deleteById(id);
    }

}
