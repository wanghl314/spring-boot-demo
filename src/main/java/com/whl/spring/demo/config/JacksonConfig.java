package com.whl.spring.demo.config;

import java.io.IOException;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
public class JacksonConfig {
    private static Logger logger = LoggerFactory.getLogger(JacksonConfig.class);

    @Bean
    public MappingJackson2HttpMessageConverter xssMappingJackson2HttpMessageConverter(Policy antisamyPolicy) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(String.class, new XssJsonDeserializer(antisamyPolicy));
        converter.getObjectMapper().registerModule(simpleModule);
        return converter;
    }

    static class XssJsonDeserializer extends JsonDeserializer<String> {
        private final Policy policy;

        XssJsonDeserializer(Policy policy) {
            this.policy = policy;
        }

        @Override
        public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            String source = jsonParser.getText().trim();

            try {
                AntiSamy antiSamy = new AntiSamy();
                CleanResults cr = antiSamy.scan(source, policy);
                source = cr.getCleanHTML();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return source;
        }

    }

}
