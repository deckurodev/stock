FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY gradlew ./
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src
RUN chmod +x gradlew && ./gradlew bootJar -x test --no-daemon
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar build/libs/*.jar"]
