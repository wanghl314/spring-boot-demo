package com.whl.spring.demo.others;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.Arrays;

public class BeanUtilsTests {

    @Test
    public void testGetPropertyDescriptors() {
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(MyClass.class);
        System.out.println(Arrays.toString(propertyDescriptors));
    }

    @Test
    public void testCopyProperties() {
        MyClass source = new MyClass();
        source.setUserId(1111L);
        MyClass2 target = new MyClass2();
        BeanUtils.copyProperties(source, target);
        System.out.println(source);
        System.out.println(target);
    }

    @Test
    public void testCopyProperties2() {
        MyClass2 source = new MyClass2();
        source.setUserId(1111L);
        MyClass target = new MyClass();
        BeanUtils.copyProperties(source, target);
        System.out.println(source);
        System.out.println(target);
    }

    static class MyClass {
        private Long userId;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        @Override
        public String toString() {
            return "MyClass{" +
                    "userId=" + userId +
                    '}';
        }

    }

    static class MyClass2 {
        private Long user_id;

        public Long getUser_id() {
            return user_id;
        }

        public void setUserId(Long user_id) {
            this.user_id = user_id;
        }

        @Override
        public String toString() {
            return "MyClass2{" +
                    "user_id=" + user_id +
                    '}';
        }

    }

}
