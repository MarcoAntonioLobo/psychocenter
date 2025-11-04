# =========================
# 🧱 Stage 1: Build
# =========================
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copia apenas o pom.xml primeiro para cachear dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Agora copia o restante do código-fonte
COPY src ./src

# Compila o projeto sem rodar testes
RUN mvn clean package -DskipTests

# =========================
# 🚀 Stage 2: Runtime
# =========================
FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app

# Copia apenas o JAR gerado no estágio anterior
COPY --from=build /app/target/psychocenter-0.0.1-SNAPSHOT.jar app.jar

# Define o perfil padrão do Spring
ENV SPRING_PROFILES_ACTIVE=dev

# Expõe a porta do app
EXPOSE 8080

# Executa a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
