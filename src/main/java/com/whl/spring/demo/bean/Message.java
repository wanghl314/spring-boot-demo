package com.whl.spring.demo.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 8314052595215999274L;

    private String uuid;
}
