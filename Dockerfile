FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG FILE=spring-boot-demo.tar.gz
COPY target/${FILE} /usr/local/${FILE}
RUN sh -c 'cd /usr/local/ && tar -zxvf /usr/local/${FILE}'
RUN sh -c 'cd /usr/local/spring-boot-demo/conf && sed -i "s/127.0.0.1/192.168.62.232/g" application.properties'
ENTRYPOINT ["sh","/usr/local/spring-boot-demo/bin/bootstrap.sh"]