FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY . .
RUN chmod +x ./mvnw && ./mvnw --batch-mode package

FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app
ENV PORT=10000
COPY --from=build --chown=10001:10001 /workspace/target/*.jar /app/app.jar
USER 10001:10001
EXPOSE 10000
ENTRYPOINT ["sh", "-c", "exec java -jar /app/app.jar --server.port=${PORT:-10000} --server.address=0.0.0.0"]
