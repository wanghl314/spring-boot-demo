package com.whl.spring.demo.config;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

@Configuration
public class JacksonConfig implements WebMvcConfigurer {
    private static Logger logger = LoggerFactory.getLogger(JacksonConfig.class);

    private final Policy antisamyPolicy;

    private final JsonMapper.Builder jsonMapperBuilder;

    public JacksonConfig(Policy antisamyPolicy, JsonMapper.Builder jsonMapperBuilder) {
        this.antisamyPolicy = antisamyPolicy;
        this.jsonMapperBuilder = jsonMapperBuilder;
    }

    @Override
    public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(String.class, new XssJsonDeserializer(this.antisamyPolicy));
        JsonMapper jsonMapper = this.jsonMapperBuilder.addModule(simpleModule).build();
        JacksonJsonHttpMessageConverter converter = new JacksonJsonHttpMessageConverter(jsonMapper);
        builder.withJsonConverter(converter);
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
