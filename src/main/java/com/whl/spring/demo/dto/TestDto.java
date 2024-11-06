package com.whl.spring.demo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class TestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -5322336020156980695L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String test;
}
