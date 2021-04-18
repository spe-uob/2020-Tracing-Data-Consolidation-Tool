FROM maven:3.6.0-jdk-11-slim AS builder

WORKDIR /usr/app

COPY src ./src
COPY pom.xml .

RUN mvn clean package


FROM openjdk:11-jre-slim

WORKDIR /usr/local/app

COPY --from=builder /usr/app/target/*.jar /usr/local/app/backend.jar

# This is where targetFile.xlsx is stored
RUN mkdir -p src/main/resources/UploadedFiles/
RUN mkdir -p src/main/resources/ProcessedFiles/

EXPOSE 8080

CMD ["java","-jar","backend.jar"]
