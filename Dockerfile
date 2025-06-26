# Étape 1 : build avec Maven + JDK 17
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copie de tout le projet, y compris libs/
COPY . .

# Installation des dépendances locales
RUN mvn install:install-file -Dfile=libs/gpsUtil.jar -DgroupId=gpsUtil -DartifactId=gpsUtil -Dversion=1.0.0 -Dpackaging=jar && \
    mvn install:install-file -Dfile=libs/RewardCentral.jar -DgroupId=rewardCentral -DartifactId=rewardCentral -Dversion=1.0.0 -Dpackaging=jar && \
    mvn install:install-file -Dfile=libs/TripPricer.jar -DgroupId=tripPricer -DartifactId=tripPricer -Dversion=1.0.0 -Dpackaging=jar

# Build du projet
RUN mvn clean package -DskipTests

# Étape 2 : image d'exécution plus légère
FROM eclipse-temurin:17-jdk
LABEL authors="zagou"
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
