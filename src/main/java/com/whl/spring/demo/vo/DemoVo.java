package com.whl.spring.demo.vo;

import java.io.Serial;
import java.io.Serializable;

import org.springframework.beans.BeanUtils;

import com.whl.spring.demo.entity.DemoEntity;

public class DemoVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -689103304748398687L;

    private Long id;

    private String title;

    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DemoEntity toEntity() {
        DemoEntity entity = new DemoEntity();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

}
