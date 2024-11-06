package com.whl.spring.demo.entity;

import com.whl.spring.demo.dto.TestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1665436630948898918L;

    private Long id;

    private String test;

    public TestDto toDto() {
        TestDto dto = new TestDto();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }

}
