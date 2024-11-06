package com.whl.spring.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public TestEntity(String test) {
        this.test = test;
    }

}
