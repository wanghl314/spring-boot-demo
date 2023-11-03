package com.whl.spring.demo.entity;

import java.io.Serial;
import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("demo")
@CompoundIndexes({
        @CompoundIndex(def = "{'id':-1}", background = true, unique = true),
        @CompoundIndex(def = "{'title':-1}", background = true)
})
public class DemoEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -3541676902442912629L;

    @Id
    private String _id;

    @Field("id")
    private Long id;

    private String title;

    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
