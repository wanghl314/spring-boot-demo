package com.whl.spring.demo.filter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class CookieHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public CookieHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public Cookie[] getCookies() {
        Cookie[] cookies = super.getCookies();

        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];

                if ("weavertest_eteamsid".equalsIgnoreCase(cookie.getName())) {
                    Cookie newCookie = new Cookie("eteamsid", cookie.getValue());
                    newCookie.setMaxAge(cookie.getMaxAge());
                    newCookie.setDomain(cookie.getDomain());
                    newCookie.setPath(cookie.getPath());
                    newCookie.setSecure(cookie.getSecure());
                    newCookie.setHttpOnly(cookie.isHttpOnly());
                    newCookie.setAttribute("SameSite", cookie.getAttribute("SameSite"));
                    cookies[i] = newCookie;
                }
            }
        }
        return cookies;
    }

}
