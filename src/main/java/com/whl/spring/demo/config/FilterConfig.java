package com.whl.spring.demo.config;

import com.whl.spring.demo.filter.CookieFilter;
import com.whl.spring.demo.filter.XssFilter;
import org.owasp.validator.html.Policy;
import org.springframework.boot.servlet.filter.OrderedFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<XssFilter> xssFilter(Policy antisamyPolicy) {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<XssFilter>();
        registration.setName("xssFilter");
        registration.setFilter(new XssFilter(antisamyPolicy));
        registration.setOrder(OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER - 10);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<CookieFilter> cookieFilter() {
        FilterRegistrationBean<CookieFilter> registration = new FilterRegistrationBean<CookieFilter>();
        registration.setName("cookieFilter");
        registration.setFilter(new CookieFilter());
        registration.setOrder(OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER);
        registration.addUrlPatterns("/*");
        return registration;
    }

}
