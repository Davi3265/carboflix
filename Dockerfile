# Imagem usada no deploy do Render (o Render não tem runtime Java nativo).
# Etapa 1: compila o projeto com o Maven Wrapper.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B -q dependency:go-offline
COPY src/ src/
# Os testes precisam de PostgreSQL; eles rodam localmente, não no build da imagem.
RUN ./mvnw -B -q -DskipTests package

# Etapa 2: só o JRE e o jar, para a imagem ficar menor.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# O plano gratuito do Render tem 512 MB: limita o heap e usa um GC mais leve.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
