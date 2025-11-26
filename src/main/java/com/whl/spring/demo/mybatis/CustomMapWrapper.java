package com.whl.spring.demo.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.MapWrapper;

import java.util.Map;

public class CustomMapWrapper extends MapWrapper {

    public CustomMapWrapper(MetaObject metaObject, Map<String, Object> map) {
        super(metaObject, map);
    }

    public String findProperty(String name, boolean useCamelCaseMapping) {
        if (StringUtils.isNotBlank(name)) {
            return name.toLowerCase();
        }
        return name;
    }

}
