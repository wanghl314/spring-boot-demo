# spring-boot-demo

- ## maven
```bash
mvn clean package
```

- ## docker
```bash
docker build -t whl/spring-boot-demo .
docker run -d -p 8080:8080 whl/spring-boot-demo
```