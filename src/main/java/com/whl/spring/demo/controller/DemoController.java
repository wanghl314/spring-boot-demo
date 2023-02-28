package com.whl.spring.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whl.spring.demo.entity.DemoEntity;
import com.whl.spring.demo.service.DemoService;
import com.whl.spring.demo.vo.DemoVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/demo")
public class DemoController {
    @Autowired
    private DemoService demoService;

    @GetMapping(value = {"", "/"})
    public String index() {
        return "Demo";
    }

    @GetMapping("/test")
    public String test(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("test", "test");
        return request.getServletContext().getRealPath("");
    }

    @GetMapping("/list")
    public List<DemoEntity> list() throws Exception {
        return this.demoService.list();
    }

    @GetMapping("/getById/{id}")
    public DemoEntity getById(@PathVariable Long id) throws Exception {
        return this.demoService.getById(id);
    }

    @PostMapping("/save")
    public int save(@RequestBody DemoVo vo) throws Exception {
        DemoEntity entity = vo.toEntity();

        if (entity.getId() == null) {
            return this.demoService.create(entity);
        } else {
            return this.demoService.update(entity);
        }
    }

    @GetMapping("/deleteById/{id}")
    public int deleteById(@PathVariable Long id) throws Exception {
        return this.demoService.deleteById(id);
    }

}
