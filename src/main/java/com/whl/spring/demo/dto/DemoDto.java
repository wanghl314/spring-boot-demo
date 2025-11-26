package com.whl.spring.demo.dto;

import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class DemoDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 5000723429162273907L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String title;

    private String content;
}
