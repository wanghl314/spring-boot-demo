package com.whl.spring.demo.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -4173511196254677517L;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Size")
    private long size;

    @JsonProperty("Last modified")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModified;

    public FileInfo(String name, long size, long lastModified) {
        this.name = name;
        this.size = size;
        this.lastModified = new Date(lastModified);
    }

}
