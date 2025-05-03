# Container Maven para compilar as extensões
FROM maven:3.9-eclipse-temurin-23-alpine AS java-compiler

# Copia a pasta de extensões para dentro do container
COPY extensions /extensions

# Compila as extensões
WORKDIR /extensions
RUN --mount=type=cache,target=/root/.m2,sharing=locked mvn clean install -DskipTests

# Copia a pasta de temas para dentro do container
COPY themes /themes

# Compila o tema
WORKDIR /themes/registrocivil
RUN jar -cf registrocivil.jar theme META-INF

# Container Keycloak
FROM quay.io/keycloak/keycloak:26.1.4

# Copia as extensões compiladas para dentro do container
COPY --from=java-compiler /extensions/keycloak-playground-extensions.jar /opt/keycloak/providers

# Copia o tema compilado previamente
COPY --from=java-compiler themes/registrocivil/registrocivil.jar /opt/keycloak/providers/registrocivil.jar

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]