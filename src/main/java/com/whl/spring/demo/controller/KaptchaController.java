package com.whl.spring.demo.controller;

import com.google.code.kaptcha.Producer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/kaptcha")
@Tag(name = "图片验证码接口", description = "图片验证码接口")
public class KaptchaController {
    private static final Map<String, String> CACHE = new ConcurrentHashMap<String, String>();

    @Autowired
    private Producer producer;

    @GetMapping({"", "/"})
    @Operation(summary = "图片验证码请求接口", description = "图片验证码请求接口")
    @Parameters({
            @Parameter(name = "key", in = ParameterIn.QUERY, required = true, description = "客户端标识，需客户端生成并保存，提交验证码的时候将key也传到后台")
    })
    public void index(HttpServletResponse response, @RequestParam String key) throws Exception {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        String kaptcha = this.producer.createText();
        CACHE.put(key, kaptcha);
        BufferedImage image = this.producer.createImage(kaptcha);

        try (ServletOutputStream out = response.getOutputStream()) {
            ImageIO.write(image, "jpg", out);
        }
    }

    @GetMapping("/verify")
    @ResponseBody
    @Operation(summary = "图片验证码验证接口", description = "图片验证码验证接口")
    @Parameters({
            @Parameter(name = "key", in = ParameterIn.QUERY, required = true, description = "客户端标识，需客户端生成并保存，提交验证码的时候将key也传到后台"),
            @Parameter(name = "text", in = ParameterIn.QUERY, required = true, description = "验证码内容")
    })
    public String verify(@RequestParam String key, @RequestParam String text) {
        String kaptcha = CACHE.get(key);

        if (text.equalsIgnoreCase(kaptcha)) {
            CACHE.remove(key);
            return "SUCCESS";
        }
        return "FAIL";
    }

}
