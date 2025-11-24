package com.whl.spring.demo.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/Default/boc_http_adp_gyzx/0.1/adapterAllHttp")
public class ESBController {
    @Autowired
    private ObjectMapper mapper;

    @RequestMapping(
            method = { RequestMethod.GET, RequestMethod.POST },
            value = {
                    "/37000001", "/37000002",
                    "/37000003", "/37000004",
                    "/37000005", "/37000006",
                    "/37000007", "/37000008",
                    "/37000009", "/37000010",
                    "/37000011", "/37000012"
            })
    public ResponseEntity<byte[]> inter37000001(HttpServletRequest request) throws Exception {
        return this.response(request);
    }

    private ResponseEntity<byte[]> response(HttpServletRequest request) throws IOException, URISyntaxException, ServletException {
        String header = request.getHeader("Individuation-Function-ID");

        if (StringUtils.equals(header, "37000011")) {
            String requestUrl = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=" +
                    request.getParameter("access_token") + "&type=" + request.getParameter("type");
            RequestEntity<?> requestEntity = createRequestEntity(request, requestUrl);
            ResponseEntity<byte[]> responseEntity = route(requestEntity);
            Map<String, String> responseString = new HashMap<>();
            responseString.put("responseString", new String(responseEntity.getBody(), StandardCharsets.UTF_8));
            List<Map<String, String>> list = new ArrayList<>();
            list.add(responseString);
            Map<String, List<Map<String, String>>> data = new HashMap<>();
            data.put("data", list);
            String jsonString = mapper.writeValueAsString(data);
            System.out.println(jsonString);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jsonString.getBytes(StandardCharsets.UTF_8));
        } else if (StringUtils.equals(header, "37000012")) {
            String requestUrl = "https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token=" +
                    request.getParameter("access_token") + "&media_id=" + request.getParameter("media_id");
            RequestEntity<?> requestEntity = createRequestEntity(request, requestUrl);
            return route(requestEntity);
        }
        String body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        String response = "header: " + header + ",body: " + body;
        Map<String, Object> json = new HashMap<>();
        json.put("errcode", 0);
        json.put("errmsg", "ok");
        json.put("access_token", response);
        json.put("expires_in", 7200);
        Map<String, String> responseString = new HashMap<>();
        responseString.put("responseString", mapper.writeValueAsString(json));
        List<Map<String, String>> list = new ArrayList<>();
        list.add(responseString);
        Map<String, List<Map<String, String>>> data = new HashMap<>();
        data.put("data", list);
        String jsonString = mapper.writeValueAsString(data);
        System.out.println(jsonString);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jsonString.getBytes(StandardCharsets.UTF_8));
    }

    private RequestEntity<?> createRequestEntity(HttpServletRequest request, String url) throws URISyntaxException, IOException, ServletException {
        String method = request.getMethod();
        HttpMethod httpMethod = HttpMethod.valueOf(method);
        String contentType = request.getContentType();
        HttpHeaders headers = parseRequestHeader(request);
        Object body = null;

        if (StringUtils.isNotBlank(contentType) && contentType.toLowerCase().trim().startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            Collection<Part> parts = request.getParts();

            if (!parts.isEmpty()) {
                String name = null;
                String filename = null;
                InputStream is = null;

                for (Part part : parts) {
                    name = part.getName();
                    filename = part.getSubmittedFileName();
                    is = part.getInputStream();
                }
                MultipartBodyBuilder builder = new MultipartBodyBuilder();
                Resource file = new ByteArrayResource(StreamUtils.copyToByteArray(is));
                builder.part(name, file).header("Content-Disposition", "form-data; name=\"" + name + "\";filename=\"" + filename + "\"");
                body = builder.build();
            }
        }

        if (body == null) {
            body = parseRequestBody(request);
        }
        return new RequestEntity<>(body, headers, httpMethod, new URI(url));
    }

    private byte[] parseRequestBody(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        return StreamUtils.copyToByteArray(inputStream);
    }

    private HttpHeaders parseRequestHeader(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        List<String> headerNames = Collections.list(request.getHeaderNames());
        for (String headerName : headerNames) {
            if (StringUtils.equalsAnyIgnoreCase(headerName, "Host", "Content-Length")) {
                continue;
            }
            List<String> headerValues = Collections.list(request.getHeaders(headerName));
            for (String headerValue : headerValues) {
                headers.add(headerName, headerValue);
            }
        }
        return headers;
    }

    private ResponseEntity<byte[]> route(RequestEntity requestEntity) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.1.122", 3128));
        requestFactory.setProxy(proxy);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate.exchange(requestEntity, byte[].class);
    }

}
