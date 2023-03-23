package com.whl.spring.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;

/**
 * druid 最新版本暂不支持spring-boot 3.0.x，需要手动处理
 */
@Configuration
@Import({ DruidDataSourceAutoConfigure.class })
public class DruidConfig {
}
