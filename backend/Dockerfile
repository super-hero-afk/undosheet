# ===== 第一阶段：构建前端 =====
FROM node:18-alpine AS frontend-build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# ===== 第二阶段：构建后端 =====
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src
# 将前端构建产物复制到后端的静态资源目录
COPY --from=frontend-build /app/dist ./src/main/resources/static
RUN mvn package -DskipTests -B

# ===== 第三阶段：运行 =====
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
# Railway 通过 PORT 环境变量指定端口，Spring Boot 会自动读取
ENTRYPOINT ["sh", "-c", "java -jar app.jar --spring.profiles.active=production --server.port=${PORT:-8080}"]
