FROM eclipse-temurin:17-jdk-alpine

# Carpeta donde se ejecutará la app dentro del contenedor
WORKDIR /app

# Copiar el JAR generado a la imagen Docker
COPY target/micro_inventario-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto donde corre Spring Boot
EXPOSE 8080

# Comando para ejecutar la API
ENTRYPOINT ["java", "-jar", "app.jar"]
