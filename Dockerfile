# -----------------------------------------------------------------------------
# Dockerfile para la API de Pacientes (Compilada con Java 22)
# -----------------------------------------------------------------------------

# Etapa 1: Base - Usa una imagen JRE (Java Runtime Environment) slim de Java 22
# Es suficiente para ejecutar la aplicación y más ligera que un JDK completo.
# 'bookworm' es una versión estable de Debian, 'bullseye' también es común.
FROM openjdk:24-ea-23-jdk-slim-bullseye
# Alternativa si prefieres bullseye: FROM openjdk:22-jre-slim-bullseye

# Etiqueta de autor (opcional pero buena práctica)
LABEL authors="aajucum aajucum@gmail.com"
ARG JAR_FILE=tarjet/*.jar
COPY ./target/api-paciente-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar", "/app.jar"]