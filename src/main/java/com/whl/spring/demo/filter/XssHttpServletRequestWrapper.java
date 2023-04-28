package com.whl.spring.demo.filter;

import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private static Logger logger = LoggerFactory.getLogger(XssHttpServletRequestWrapper.class);

    private Policy policy;

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public XssHttpServletRequestWrapper(HttpServletRequest request, Policy policy) {
        this(request);
        this.policy = policy;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);

        if (StringUtils.isEmpty(value)) {
            return value;
        }
        return this.cleanXSS(value);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> enumeration = super.getHeaders(name);

        if (!enumeration.hasMoreElements()) {
            return enumeration;
        }
        Vector<String> vector = new Vector<String>();

        while (enumeration.hasMoreElements()) {
            String value = enumeration.nextElement();
            vector.add(this.cleanXSS(value));
        }
        return vector.elements();
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);

        if (StringUtils.isEmpty(value)) {
            return value;
        }
        return this.cleanXSS(value);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = super.getParameterMap();

        if (MapUtils.isEmpty(map)) {
            return map;
        }

        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            String[] values = entry.getValue();

            if (ArrayUtils.isNotEmpty(values)) {
                for (int i = 0; i < values.length; i++) {
                    values[i] = this.cleanXSS(values[i]);
                }
            }
        }
        return map;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);

        if (ArrayUtils.isEmpty(values)) {
            return values;
        }
        int length = values.length;
        String[] escapseValues = new String[length];

        for (int i = 0; i < length; i++) {
            escapseValues[i] = this.cleanXSS(values[i]);
        }
        return escapseValues;
    }

    private String cleanXSS(String data) {
        if (policy == null) {
            return data;
        }

        try {
            AntiSamy antiSamy = new AntiSamy();
            CleanResults cr = antiSamy.scan(data, policy);
            data = cr.getCleanHTML();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return data;
    }

}
