package com.whl.spring.demo.entity;

import com.whl.spring.demo.dto.DemoDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serial;
import java.io.Serializable;

@Document("demo")
@CompoundIndexes({
        @CompoundIndex(def = "{'id':-1}", background = true, unique = true),
        @CompoundIndex(def = "{'title':-1}", background = true)
})
@Getter
public class DemoEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -3541676902442912629L;

    @Id
    @Field("_id")
    private String objectId;

    @Field("id")
    @Setter
    private Long id;

    @Setter
    private String title;

    @Setter
    private String content;

    public DemoDto toDto() {
        DemoDto dto = new DemoDto();
        BeanUtils.copyProperties(this, dto, "objectId");
        return dto;
    }

}
