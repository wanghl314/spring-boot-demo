package com.whl.spring.demo.event;

import com.alibaba.druid.DbType;
import com.alibaba.druid.util.JdbcUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;

public class CustomApplicationListener implements GenericApplicationListener {

    @Override
    public boolean supportsEventType(ResolvableType eventType) {
        return eventType.getRawClass() != null
                && ApplicationEnvironmentPreparedEvent.class.isAssignableFrom(eventType.getRawClass());
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (!(event instanceof ApplicationEnvironmentPreparedEvent environmentPreparedEvent)) {
            return;
        }
        ConfigurableEnvironment environment = environmentPreparedEvent.getEnvironment();
        this.initDataSourceProperties(environment);
    }

    private void initDataSourceProperties(ConfigurableEnvironment environment) {
        String url = environment.getProperty("spring.datasource.druid.url");

        if (StringUtils.isBlank(url)) {
            url = environment.getProperty("spring.datasource.url");
        }

        if (StringUtils.isNotBlank(url)) {
            String driverClassName = environment.getProperty("spring.datasource.druid.driver-class-name");

            if (StringUtils.isBlank(driverClassName)) {
                driverClassName = environment.getProperty("spring.datasource.driver-class-name");
            }
            DbType dbType = JdbcUtils.getDbTypeRaw(url, driverClassName);

            if (dbType != null) {
                String validationQuery;

                if (dbType == DbType.oracle) {
                    validationQuery = "select 1 from dual";
                } else {
                    validationQuery = "select 1";
                }
                String filters;

                if (dbType == DbType.dm) {
                    filters = "config,stat";
                } else {
                    filters = "config,stat,wall";
                }
                System.setProperty("spring.datasource.druid.validation-query", validationQuery);
                System.setProperty("spring.datasource.druid.filters", filters);
                System.out.println(environment.getProperty("spring.datasource.druid.validation-query"));
                System.out.println(environment.getProperty("spring.datasource.druid.filters"));
            }
        }
    }

}
