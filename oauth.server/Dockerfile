FROM amazoncorretto:17-alpine

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
# Ensure the Maven wrapper is executable (fixes exit code 126 on some filesystems)
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests

# Run the application
# Cloud Run requiere que el servicio escuche en el puerto definido por la variable PORT
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-9000} -jar target/oauth-server-0.0.1-SNAPSHOT.jar"]

