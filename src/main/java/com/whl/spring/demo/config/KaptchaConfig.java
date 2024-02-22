package com.whl.spring.demo.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

	@Bean
	public Producer producer() {
		Properties properties = new Properties();
		properties.put("kaptcha.border", "no");
		properties.put("kaptcha.image.width", "150");
		properties.put("kaptcha.image.height", "42");
		properties.put("kaptcha.textproducer.font.size", "36");
		properties.put("kaptcha.textproducer.char.string", "1234567890abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ");
		properties.put("kaptcha.textproducer.char.length", "4");
		properties.put("kaptcha.textproducer.char.space", "5");
		properties.put("kaptcha.noise.impl", "com.google.code.kaptcha.impl.DefaultNoise");
		properties.put("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple");
		properties.put("kaptcha.noise.color", "black");
		Config config = new Config(properties);
		DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
		defaultKaptcha.setConfig(config);
		return defaultKaptcha;
	}

}
