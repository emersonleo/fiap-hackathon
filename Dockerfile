# Multi-stage build para otimizar imagem final
FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Diretório de trabalho
WORKDIR /app

# Copiar pom.xml e baixar dependências (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests

# Estágio final - runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar JAR da aplicação
COPY --from=build /app/target/*.jar application.jar

# CRÍTICO: Copiar explicitamente os arquivos de migration
# Isso garante que os scripts SQL estejam disponíveis no container
COPY src/main/resources/db/migration /app/BOOT-INF/classes/db/migration

# Expor porta
EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Executar aplicação
ENTRYPOINT ["java", "-jar", "/app/application.jar"]