# 多阶段构建Dockerfile示例
FROM maven:3.8-openjdk-8 AS build

WORKDIR /app

# 复制pom文件
COPY pom.xml .
COPY common/pom.xml ./common/
COPY gateway/pom.xml ./gateway/
COPY auth-service/pom.xml ./auth-service/
COPY user-service/pom.xml ./user-service/
COPY order-service/pom.xml ./order-service/
COPY seckill-service/pom.xml ./seckill-service/

# 下载依赖
RUN mvn dependency:go-offline -B

# 复制源代码
COPY . .

# 构建项目
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:8-jre-slim

WORKDIR /app

# 复制jar包
COPY --from=build /app/*/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

