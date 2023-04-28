package com.whl.spring.demo.filter;

import java.io.IOException;

import org.owasp.validator.html.Policy;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class XssFilter implements Filter {
    private Policy antisamyPolicy;

    public XssFilter(Policy antisamyPolicy) {
        this.antisamyPolicy = antisamyPolicy;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request, this.antisamyPolicy), response);
    }

    @Override
    public void destroy() {

    }

}
