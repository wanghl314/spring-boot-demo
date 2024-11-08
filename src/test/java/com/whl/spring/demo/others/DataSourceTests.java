package com.whl.spring.demo.others;

import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.DatabaseDriver;

public class DataSourceTests {
    private static String[] urls;

    @BeforeAll
    public static void init() {
        urls = new String[] {
                "jdbc:mysql://127.0.0.1:3306/spring_boot_demo?autoReconnect=true&characterEncoding=UTF-8&failOverReadOnly=false&serverTimezone=GMT%2B8&useSSL=true&useUnicode=true&verifyServerCertificate=false&zeroDateTimeBehavior=convertToNull",
                "jdbc:oracle:thin:@192.168.1.124:1521:orcl",
                "jdbc:sqlserver://10.12.2.18:1433;DatabaseName=ebridge",
                "jdbc:postgresql://10.12.103.23:5432/ecology10?connectTimeout=0&socketTimeout=0&stringtype=unspecified",
                "jdbc:dm://10.12.103.20:5236"
        };
    }

    @Test
    public void testGetDriverFromUrl() {
        for (String url : urls) {
            DatabaseDriver driver = DatabaseDriver.fromJdbcUrl(url);
            System.out.println(driver);
        }
    }

    @Test
    public void testGetDbTypeFromUrl() {
        for (String url : urls) {
            String dbTypeName = JdbcUtils.getDbType(url, null);
            System.out.println(dbTypeName);
        }
    }

}
