package com.whl.spring.demo.entity;

import com.whl.spring.demo.dto.UserDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class UserEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 255122426203916350L;

    private Long id;

    private String username;

    private int age;

    public UserDto toDto() {
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }

}
