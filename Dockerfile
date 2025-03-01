# 1. Chọn base image Maven để build ứng dụng
FROM maven:3.9.4-eclipse-temurin-21 AS builder

# 2. Copy toàn bộ mã nguồn vào container
WORKDIR /app
COPY . .

# 3. Build ứng dụng bằng Maven
RUN mvn clean package -DskipTests

# 4. Dùng image OpenJDK để chạy ứng dụng
FROM openjdk:21-jdk-slim

# 5. Copy file JAR từ image builder vào image chạy
WORKDIR /app
COPY --from=builder /app/target/gotNow-0.0.1-SNAPSHOT.jar /app/app.jar

# 6. Expose cổng ứng dụng
EXPOSE 8080

# 7. Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "/app/app.jar"]