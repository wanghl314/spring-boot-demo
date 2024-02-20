FROM centos:oraclejdk-21
VOLUME /tmp
ARG FILE=spring-boot-demo.tar.gz
COPY target/${FILE} /usr/local/${FILE}
RUN sh -c 'cd /usr/local/ && tar -zxvf /usr/local/${FILE}'
RUN sh -c 'cd /usr/local/spring-boot-demo/conf && sed -i "s/127.0.0.1/192.168.62.232/g" application.properties'
RUN sh -c 'cd /usr/local/spring-boot-demo/conf && sed -i "s/^arthas.home=.*$/#&/g" application.properties'
RUN sh -c 'cd /usr/local/spring-boot-demo/conf && sed -i "/^#arthas.ip=.*$/s/^#//" application.properties'
ENV JAVA_HOME=/usr/java/jdk-21.0.1
ENTRYPOINT ["sh","/usr/local/spring-boot-demo/bin/bootstrap.sh"]