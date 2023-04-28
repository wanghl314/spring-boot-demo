package com.whl.spring.demo.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.whl.spring.demo.filter.XssFilter;

@Configuration
public class AntisamyConfig {

    @Bean
    public FilterRegistrationBean<XssFilter> xssFilter() {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<XssFilter>();
        registration.setName("xssFilter");
        registration.setFilter(new XssFilter());
        registration.setOrder(OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER);
        registration.addUrlPatterns("/*");
        return registration;
    }

}
