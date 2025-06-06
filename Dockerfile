# Use uma imagem base do OpenJDK 17 (ou a versão que você usa)
FROM eclipse-temurin:17-jdk-alpine

# Defina o diretório dentro do container
WORKDIR /app

# Copie o arquivo .jar gerado para dentro do container
COPY build/libs/conversor-moeda-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta que a aplicação usa (padrão 8080)
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
