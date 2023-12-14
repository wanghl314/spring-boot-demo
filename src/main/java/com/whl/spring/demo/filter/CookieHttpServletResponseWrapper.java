package com.whl.spring.demo.filter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class CookieHttpServletResponseWrapper extends HttpServletResponseWrapper {

    public CookieHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void addCookie(Cookie cookie) {
        if ("eteamsid".equalsIgnoreCase(cookie.getName())) {
            Cookie newCookie = new Cookie("weavertest_eteamsid", cookie.getValue());
            newCookie.setMaxAge(cookie.getMaxAge());
            newCookie.setDomain(cookie.getDomain());
            newCookie.setPath(cookie.getPath());
            newCookie.setSecure(cookie.getSecure());
            newCookie.setHttpOnly(cookie.isHttpOnly());
            newCookie.setAttribute("SameSite", cookie.getAttribute("SameSite"));
            super.addCookie(newCookie);
        } else {
            super.addCookie(cookie);
        }
    }

}
