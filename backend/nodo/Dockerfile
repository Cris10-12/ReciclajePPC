# ---- Etapa 1: compilar el fat-jar con Gradle ----
FROM gradle:8.14-jdk21 AS build
WORKDIR /app
COPY . .
# buildFatJar es la tarea del plugin de Ktor: genera un jar ejecutable con todas las dependencias.
RUN gradle buildFatJar --no-daemon

# ---- Etapa 2: imagen liviana solo con el runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
