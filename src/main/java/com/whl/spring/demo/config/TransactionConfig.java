package com.whl.spring.demo.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

//@Configuration
public class TransactionConfig {
    public static final String JDBC_TRANSACTION_MANAGER_NAME = "transactionManager";

    public static final String MONGO_TRANSACTION_MANAGER_NAME = "mongoTransactionManager";

    public static final String JDBC_TRANSACTIONTEMPLATE_NAME = "transactionTemplate";

    public static final String MONGO_TRANSACTIONTEMPLATE_NAME = "mongoTransactionTemplate";

    @Primary
    @Bean(JDBC_TRANSACTION_MANAGER_NAME)
    DataSourceTransactionManager dataSourceTransactionManager(Environment environment,
                                                              DataSource dataSource,
                                                              ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        DataSourceTransactionManager transactionManager;

        if (environment.getProperty("spring.dao.exceptiontranslation.enabled", Boolean.class, Boolean.TRUE)) {
            transactionManager = new JdbcTransactionManager(dataSource);
        } else {
            transactionManager = new DataSourceTransactionManager(dataSource);
        }
        transactionManagerCustomizers.ifAvailable((customizers) -> {
            customizers.customize((TransactionManager) transactionManager);
        });
        return transactionManager;
    }

    @Bean(MONGO_TRANSACTION_MANAGER_NAME)
    public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory databaseFactory) {
        return new MongoTransactionManager(databaseFactory);
    }

    @Primary
    @Bean(JDBC_TRANSACTIONTEMPLATE_NAME)
    public TransactionTemplate transactionTemplate(DataSourceTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean(MONGO_TRANSACTIONTEMPLATE_NAME)
    public TransactionTemplate mongoTransactionTemplate(MongoTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

}
