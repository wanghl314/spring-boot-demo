package com.whl.spring.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.bean.DeptQuery;
import com.whl.spring.demo.dto.DeptDto;
import com.whl.spring.demo.entity.DeptEntity;
import com.whl.spring.demo.service.DeptService;
import com.whl.spring.demo.vo.DeptVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/dept")
public class DeptController {
    @Autowired
    private DeptService deptService;

    @GetMapping(value = {"", "/"})
    public String index() {
        return "Dept";
    }

    @PostMapping(value = "/list")
    public Page<DeptDto> list(@RequestBody @Validated DeptQuery query) throws Exception {
        Page<DeptDto> result = new Page<>();
        Page<DeptEntity> data = this.deptService.list(new Page<DeptEntity>(query.getPageNo(), query.getPageSize()));

        if (data != null) {
            BeanUtils.copyProperties(data, result, "records");

            if (data.getRecords() != null) {
                result.setRecords(data.getRecords().stream().map(DeptEntity::toDto).collect(Collectors.toList()));
            }
        }
        return result;
    }

    @GetMapping("/getById/{id}")
    public DeptDto getById(@PathVariable Long id) throws Exception {
        DeptEntity entity = this.deptService.getById(id);

        if (entity != null) {
            return entity.toDto();
        }
        return null;
    }

    @PostMapping("/save")
    public DeptDto save(@RequestBody DeptVo vo) throws Exception {
        DeptEntity entity = vo.toEntity();
        int result;

        if (vo.getId() == null) {
            result = this.deptService.create(entity);
        } else {
            result = this.deptService.update(entity);
        }

        if (result > 0) {
            return entity.toDto();
        }
        return null;
    }

    @GetMapping("/deleteById/{id}")
    public int deleteById(@PathVariable Long id) throws Exception {
        return this.deptService.deleteById(id);
    }

}
