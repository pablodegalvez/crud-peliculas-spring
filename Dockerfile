# --- Etapa 1: Compilación usando el Wrapper ---
FROM eclipse-temurin:17-jdk AS constructor
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests

# --- Etapa 2: Imagen final ligera ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=constructor /app/target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]