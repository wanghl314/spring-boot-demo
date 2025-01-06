package com.whl.spring.demo.stream;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class JDKStreamTests {

    @Test
    public void test1() {
        List<String> adminUsers = new ArrayList<>();
        adminUsers.add("admin");
        adminUsers.add("111");
        adminUsers.add("222");
        adminUsers.add("333");
        adminUsers.add("444");
        List<Long> employeeIdList = new ArrayList<>();

        adminUsers.forEach(e -> {
            try {
                employeeIdList.add(Long.parseLong(e));
            } catch (NumberFormatException ignored) {}
        });
        System.out.println(employeeIdList);
    }

}
