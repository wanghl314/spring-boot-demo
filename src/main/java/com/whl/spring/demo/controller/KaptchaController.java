package com.whl.spring.demo.controller;

import com.google.code.kaptcha.Producer;
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
public class KaptchaController {
    private static final Map<String, String> CACHE = new ConcurrentHashMap<String, String>();

    @Autowired
    private Producer producer;

    @GetMapping({"", "/"})
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
    public String verify(@RequestParam String key, @RequestParam String text) {
        String kaptcha = CACHE.get(key);

        if (text.equalsIgnoreCase(kaptcha)) {
            CACHE.remove(key);
            return "SUCCESS";
        }
        return "FAIL";
    }

}
