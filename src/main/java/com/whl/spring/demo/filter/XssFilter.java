package com.whl.spring.demo.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.owasp.validator.html.Policy;

import java.io.IOException;

public class XssFilter implements Filter {
    private final Policy antisamyPolicy;

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
