FROM eclipse-temurin
VOLUME /tmp
ARG FILE=spring-boot-demo.tar.gz
COPY target/${FILE} /usr/local/${FILE}
RUN sh -c 'cd /usr/local/ && tar -zxvf /usr/local/${FILE}'
COPY conf/application.properties /usr/local/spring-boot-demo/conf
RUN sh -c 'cd /usr/local/spring-boot-demo/conf && sed -i "s/127.0.0.1/192.168.29.19/g" application.properties'
RUN sh -c 'cd /usr/local/spring-boot-demo/conf && sed -i "s/^arthas.home=.*$/arthas.home=/g" application.properties'
RUN sh -c 'cd /usr/local/spring-boot-demo/conf && sed -i "s/^arthas.ip=$/arthas.ip=0.0.0.0/g" application.properties'
# ENV JAVA_HOME=/usr/java/jdk-21.0.1
ENTRYPOINT ["sh","/usr/local/spring-boot-demo/bin/bootstrap.sh"]