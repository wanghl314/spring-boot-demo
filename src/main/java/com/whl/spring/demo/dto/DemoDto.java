package com.whl.spring.demo.dto;

import java.io.Serial;
import java.io.Serializable;

public class DemoDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 5000723429162273907L;

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

}
