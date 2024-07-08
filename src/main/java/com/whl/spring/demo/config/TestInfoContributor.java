package com.whl.spring.demo.config;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TestInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("a", RandomStringUtils.randomAlphabetic(10));
        info.put("b", RandomStringUtils.randomAlphanumeric(10));
        info.put("c", RandomStringUtils.randomAscii(10));
        info.put("d", RandomStringUtils.randomNumeric(10));
        builder.withDetails(info);
    }

}
