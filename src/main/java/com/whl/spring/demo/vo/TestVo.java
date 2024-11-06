package com.whl.spring.demo.vo;

import com.whl.spring.demo.entity.TestEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class TestVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -5017903900586217827L;

    private Long id;

    private String test;

    public TestEntity toEntity() {
        TestEntity entity = new TestEntity();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

}
