FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD socialfeed-0.0.1.jar app.jar
ADD lib lib
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]