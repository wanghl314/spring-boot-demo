FROM eclipse-temurin
VOLUME /tmp
ARG FILE=spring-boot-demo.tar.gz
COPY target/${FILE} /usr/local/${FILE}
RUN sh -c 'cd /usr/local/ && tar -zxvf /usr/local/${FILE}'
# ENV JAVA_HOME=/usr/java/jdk-21.0.1
ENTRYPOINT ["sh","/usr/local/spring-boot-demo/bin/bootstrap.sh"]