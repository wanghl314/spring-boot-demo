package com.whl.spring.demo.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class PGVectorDemoEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -4031780434446023516L;

    private Long id;

    private String content;

    private String embedding; // 存 [1,2,3] 格式字符串即可
}
