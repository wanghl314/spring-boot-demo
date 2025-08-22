# spring-boot-demo

- ## local run
    * Working directory: ` $MODULE_WORKING_DIR$ `
    * Program arguments: ` --spring.config.additional-location=file:${user.dir}\conf\ --logging.config=${user.dir}\conf\logback-spring.xml `

- ## maven
```bash
mvn clean package -DskipTests=true
```

- ## docker
centos:oraclejdk-21是由centos镜像改造而来的，增加了oraclejdk-21
```bash
docker build -t whl/spring-boot-demo .
docker run -d -p 8080:8080 -p 8563:8563 -p 8180:8180 -v E:\docker\spring-boot-demo\arthas:/usr/local/spring-boot-demo/arthas -v E:\docker\spring-boot-demo\conf:/usr/local/spring-boot-demo/conf -v E:\docker\spring-boot-demo\logs:/usr/local/spring-boot-demo/logs whl/spring-boot-demo
```

- ## redission
本项目只引入下面的依赖，目的是只想使用redisson的分布式锁功能，并不想更改RedisTemplate底层组件
```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>${redisson.version}</version>
</dependency>
```
如需更改底层组件需要引入下面的依赖，并移除[RedissonConfig.java](./src/main/java/com/whl/spring/demo/config/RedissonConfig.java)
```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>${redisson.version}</version>
</dependency>
```
redission自动装配相关代码位置：redisson-spring-boot-starter-${redisson.version}.jar!\org\redisson\spring\starter\RedissonAutoConfiguration