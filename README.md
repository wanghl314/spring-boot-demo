# spring-boot-demo

- ## maven
```bash
mvn clean package
```

- ## docker
```bash
docker build -t whl/spring-boot-demo .
docker run -d -p 8080:8080 -p 8563:8563 whl/spring-boot-demo
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
如需更改底层组件需要引入下面的依赖，并移除[RedissionConfig.java](./src/main/java/com/whl/spring/demo/config/RedissionConfig.java)
```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>${redisson.version}</version>
</dependency>
```
redission自动装配相关代码位置：redisson-spring-boot-starter-${redisson.version}.jar!\org\redisson\spring\starter\RedissonAutoConfiguration