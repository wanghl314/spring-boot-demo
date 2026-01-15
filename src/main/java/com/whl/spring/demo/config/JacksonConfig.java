package com.whl.spring.demo.config;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

@Configuration
public class JacksonConfig {
    private static Logger logger = LoggerFactory.getLogger(JacksonConfig.class);

    @Bean
    public JacksonJsonHttpMessageConverter xssMappingJackson2HttpMessageConverter(Policy antisamyPolicy, JsonMapper.Builder builder) {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(String.class, new XssJsonDeserializer(antisamyPolicy));
        JsonMapper jsonMapper = builder.addModule(simpleModule).build();
        return new JacksonJsonHttpMessageConverter(jsonMapper);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
        return new ByteArrayHttpMessageConverter();
    }

    static class XssJsonDeserializer extends ValueDeserializer<String> {
        private final Policy policy;

        XssJsonDeserializer(Policy policy) {
            this.policy = policy;
        }

        @Override
        public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws JacksonException {
            String source = jsonParser.getString().trim();

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
