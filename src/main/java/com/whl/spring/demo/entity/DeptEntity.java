package com.whl.spring.demo.entity;

import com.whl.spring.demo.dto.DeptDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class DeptEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -6761877145506415811L;

    private Long id;

    private String name;

    private String showorder;

    public DeptDto toDto() {
        DeptDto dto = new DeptDto();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }

}
