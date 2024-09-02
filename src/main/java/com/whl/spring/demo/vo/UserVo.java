package com.whl.spring.demo.vo;

import com.whl.spring.demo.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class UserVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -9122414045682007309L;

    private Long id;

    private String username;

    private int age;

    public UserEntity toEntity() {
        UserEntity entity = new UserEntity();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

}
