package com.whl.spring.demo.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class MybatisConfig {
    private static Logger logger = LoggerFactory.getLogger(MybatisConfig.class);

    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        Properties properties = new Properties();
        properties.setProperty("MySQL", DbType.MYSQL.getDb());
        properties.setProperty("Oracle", DbType.ORACLE.getDb());
        properties.setProperty("DM DBMS", DbType.ORACLE.getDb());
        properties.setProperty("SQL Server", DbType.SQL_SERVER.getDb());
        properties.setProperty("PostgreSQL", DbType.POSTGRE_SQL.getDb());
        DatabaseIdProvider dip = new VendorDatabaseIdProvider();
        dip.setProperties(properties);
        return dip;
    }

    @Configuration
    static class MybatisPlusConfig {

        @Bean
        public MybatisPlusInterceptor mybatisPlusInterceptor(DataSource dataSource,
                                                             DatabaseIdProvider databaseIdProvider) throws Exception {
            String databaseId = databaseIdProvider.getDatabaseId(dataSource);
            DbType dbType = DbType.getDbType(databaseId);
            logger.info("{}", dbType);
            MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));
            return interceptor;
        }

    }

}
