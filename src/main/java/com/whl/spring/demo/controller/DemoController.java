package com.whl.spring.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whl.spring.demo.dto.DemoDto;
import com.whl.spring.demo.entity.DemoEntity;
import com.whl.spring.demo.service.DemoService;
import com.whl.spring.demo.vo.DemoVo;

import jakarta.servlet.http.Cookie;
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
        Cookie[] cookies = request.getCookies();
        String cookieName = "eteamsid";
        boolean setCookie = true;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equalsIgnoreCase(cookie.getName())) {
                    setCookie = false;
                    break;
                }
            }
        }

        if (setCookie) {
            Cookie cookie = new Cookie(cookieName, "xxx");
            response.addCookie(cookie);
        }
        response.setHeader("test", "test");
        return request.getServletContext().getRealPath("");
    }

    @GetMapping("/list")
    public List<DemoDto> list() throws Exception {
        List<DemoEntity> list = this.demoService.list();
        List<DemoDto> result = null;

        if (list != null) {
            result = list.stream().map(DemoEntity::toDto).collect(Collectors.toList());
        }
        return result;
    }

    @GetMapping("/getById/{id}")
    public DemoDto getById(@PathVariable Long id) throws Exception {
        DemoEntity entity = this.demoService.getById(id);
        DemoDto result = null;

        if (entity != null) {
            result = entity.toDto();
        }
        return result;
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
