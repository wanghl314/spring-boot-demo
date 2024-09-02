package com.whl.spring.demo.config;

import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class AntisamyConfig {
    private static final String POLICY_FILE = "antisamy-ebay.xml";

    private static Logger logger = LoggerFactory.getLogger(AntisamyConfig.class);

    @Bean
    public Policy antisamyPolicy() throws PolicyException {
        Policy policy;

        try {
            policy = Policy.getInstance(new ClassPathResource(POLICY_FILE).getInputStream());
        } catch (Exception e) {
            logger.warn("fallback to default policy", e);
            policy = Policy.getInstance();
        }
        return policy;
    }

}
