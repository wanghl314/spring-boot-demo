package com.whl.spring.demo.mybatis;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

import java.util.Map;

public class CustomMapWrapperFactory implements ObjectWrapperFactory {

    public boolean hasWrapperFor(Object object) {
        return object instanceof Map;
    }

    @SuppressWarnings("unchecked")
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        return new CustomMapWrapper(metaObject, (Map<String, Object>) object);
    }

}
