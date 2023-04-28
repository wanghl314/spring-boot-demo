package com.whl.spring.demo.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private static Logger logger = LoggerFactory.getLogger(XssHttpServletRequestWrapper.class);

    private static final int CLEAN_DEPTH = 10;

    private static final String POLICY_FILE = "antisamy-ebay.xml";

    private static ObjectMapper mapper = new ObjectMapper();

    private static volatile Policy policy = null;

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);

        try {
            if (policy == null) {
                try {
                    policy = Policy.getInstance(new ClassPathResource(POLICY_FILE).getInputStream());
                } catch (Exception e) {
                    policy = Policy.getInstance();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
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

    @Override
    public ServletInputStream getInputStream() throws IOException {
        String contentType = super.getContentType();

        if (StringUtils.isBlank(contentType) || !StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE)) {
            return super.getInputStream();
        }
        String jsonString = IOUtils.toString(super.getInputStream(), "UTF-8");

        if (StringUtils.isBlank(jsonString)) {
            return super.getInputStream();
        }
        Map jsonMap = null;

        try {
            jsonMap = mapper.readValue(jsonString, Map.class);
        } catch (JsonProcessingException e) {
            return super.getInputStream();
        }
        jsonMap = this.cleanXSS(jsonMap);
        jsonString = mapper.writeValueAsString(jsonMap);
        final ByteArrayInputStream bis = new ByteArrayInputStream(jsonString.getBytes("UTF-8"));

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return bis.read();
            }
        };
    }

    private Map<Object, Object> cleanXSS(Map<Object, Object> data) {
        return this.cleanXSS(data, 1);
    }

    private Map<Object, Object> cleanXSS(Map<Object, Object> data, int deep) {
        if (policy == null || deep > CLEAN_DEPTH) {
            return data;
        }

        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof String) {
                String string = (String) value;
                entry.setValue(this.cleanXSS(string));
            } else if (value instanceof List) {
                List<Object> list = (List<Object>) value;
                int i = 0;
                Iterator<Object> iterator = list.iterator();

                while (iterator.hasNext()) {
                    Object element = iterator.next();

                    if (element instanceof String) {
                        String string = (String) element;
                        list.set(i, this.cleanXSS(string));
                    }
                    i++;
                }
            } else if (value instanceof Map) {
                Map<Object, Object> map = (Map<Object, Object>) value;
                entry.setValue(this.cleanXSS(map, ++deep));
            }
        }
        return data;
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
