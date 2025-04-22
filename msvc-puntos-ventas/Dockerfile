# Etapa 1: Construcción de libs-msvc-commons
FROM maven:3.9.9-eclipse-temurin-21-alpine AS commons-builder
WORKDIR /app/commons

# Copiar el código fuente de libs-msvc-commons
COPY libs-msvc-commons/pom.xml pom.xml
COPY libs-msvc-commons/src src

# Construir la librería e instalarla en el repositorio local de Maven
RUN mvn clean install -DskipTests

# Etapa 2: Construcción del microservicio (con Maven)
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app/microservice

# Copiar el código fuente del microservicio
COPY msvc-puntos-ventas/pom.xml pom.xml
COPY msvc-puntos-ventas/src src

# Copiar el repositorio Maven de la etapa anterior para que el microservicio lo encuentre
COPY --from=commons-builder /root/.m2 /root/.m2

# Descargar dependencias y construir el microservicio
RUN mvn dependency:go-offline
RUN mvn clean package -DskipTests

# Etapa 3: Imagen Final (runtime con OpenJDK)
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copiar el JAR del microservicio
COPY --from=builder /app/microservice/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
