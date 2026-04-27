FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -DskipTests org.apache.maven.plugins:maven-dependency-plugin:3.6.1:go-offline

COPY src ./src
RUN mvn -q -DskipTests package org.apache.maven.plugins:maven-dependency-plugin:3.6.1:copy-dependencies

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/classes ./target/classes
COPY --from=build /app/target/dependency ./target/dependency
COPY src/main/resources ./src/main/resources
COPY entrypoint.sh /__cacert_entrypoint.sh

RUN chmod +x /__cacert_entrypoint.sh

ENV MAIN_CLASS=com.enviotxt.sftp.sender.SftpSenderApplication

ENTRYPOINT ["/__cacert_entrypoint.sh"]
CMD ["sh","-c","java -cp 'target/classes:target/dependency/*:src/main/resources' \"$MAIN_CLASS\""]
