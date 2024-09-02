package com.whl.spring.demo.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class UserQuery implements Serializable {
    @Serial
    private static final long serialVersionUID = -6504289870919303122L;

    @Min(1)
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", description = "当前页，默认 1")
    private int pageNo = 1;

    @Min(1)
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", description = "每页显示条数，默认 10")
    private int pageSize = 10;
}
