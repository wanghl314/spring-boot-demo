package com.whl.spring.demo.vo;

import com.whl.spring.demo.entity.DemoEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class DemoVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -689103304748398687L;

    private Long id;

    private String title;

    private String content;

    public DemoEntity toEntity() {
        DemoEntity entity = new DemoEntity();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

}
