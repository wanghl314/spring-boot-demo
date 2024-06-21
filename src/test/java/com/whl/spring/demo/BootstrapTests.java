package com.whl.spring.demo;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Rollback
@SpringBootTest(args = {
        "--spring.config.additional-location=file:${user.dir}\\conf\\",
        "--logging.config=${user.dir}\\conf\\logback-spring.xml"
})
public class BootstrapTests {

}
