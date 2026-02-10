package com.whl.spring.demo.vo;

import com.whl.spring.demo.entity.DeptEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class DeptVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -8774710139484958972L;

    private Long id;

    private String name;

    private String showorder;

    public DeptEntity toEntity() {
        DeptEntity entity = new DeptEntity();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

}
