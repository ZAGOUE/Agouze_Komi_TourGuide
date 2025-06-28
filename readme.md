# Projet TourGuide

## 🔧 Intégration Continue

[![CI](https://github.com/ZAGOUE/Agouze_Komi_TourGuide/actions/workflows/ci.yml/badge.svg)](https://github.com/ZAGOUE/Agouze_Komi_TourGuide/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/ZAGOUE/Agouze_Komi_TourGuide/branch/master/graph/badge.svg)](https://codecov.io/gh/ZAGOUE/Agouze_Komi_TourGuide)

[📚 Documentation](https://zagoue.github.io/Agouze_Komi_TourGuide/)
---

## ⚙️ Technologies

- Java 17
- Spring Boot 3.x
- JUnit 5
- Docker / GitHub Actions

---

## 📦 Installation des dépendances locales (gpsUtil, RewardCentral, TripPricer)

Ces librairies tierces ne sont pas disponibles sur Maven Central.

**Avant de compiler ou de dockeriser le projet, exécutez** :

```bash
mvn install:install-file -Dfile=libs/gpsUtil.jar -DgroupId=gpsUtil -DartifactId=gpsUtil -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=libs/RewardCentral.jar -DgroupId=rewardCentral -DartifactId=rewardCentral -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=libs/TripPricer.jar -DgroupId=tripPricer -DartifactId=tripPricer -Dversion=1.0.0 -Dpackaging=jar
```

> Ces JAR sont ensuite déclarés comme dépendances Maven **standard** dans le `pom.xml`.

---



## Architecture

```

Projet_8_TourGuide/
├── libs/
│   ├── gpsUtil.jar
│   ├── RewardCentral.jar
│   └── TripPricer.jar
├── src/
│   ├── main/java
│   ├── test/java
│   
├── pom.xml
├── Dockerfile
├── README.md
├── TESTING.md
└── ...

````


## Fonctionnalités clés

- Suivi de la position des utilisateurs (simulation `gpsUtil`)
- Attribution de récompenses automatiques (via `RewardsCentral`)
- Recommandations touristiques (via `TripPricer`)
- Tests unitaires avec JaCoCo
- Tests de performance (jusqu’à 100k utilisateurs)
- CI/CD avec GitHub Actions

---


### Commandes utiles

```bash
# Compilation du projet
mvn clean install

# Lancement des tests unitaires
mvn test

# Génération du rapport JaCoCo
mvn jacoco:report
````

---

## Couverture de tests

Le rapport JaCoCo est généré dans :

```
target/site/jacoco/index.html
```

---

## Tests de performance

> Le test `TestPerformance.java` est conçu pour évaluer la scalabilité du système.
> Il est **exclu du cycle CI classique** pour ne pas ralentir l'intégration continue.

---

## CI/CD – GitHub Actions

Le pipeline CI se déclenche automatiquement :

* À chaque `push` ou `pull_request` sur la branche `master`

Il comprend :
- Compilation
- Tests unitaires
- Analyse de couverture
- Build Docker
- Génération de la documentation


---
## 🐳 Docker

### Construction de l'image

```bash
docker build -t tourguide-app .
```

### Exécution du conteneur

```bash
docker run -p 8080:8080 tourguide-app
```

### Dockerfile utilisé

```Dockerfile
# Étape 1 : build avec Maven + JDK 17
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copie du projet avec les libs

COPY . .
```
# Installation des dépendances locales
RUN mvn install:install-file -Dfile=libs/gpsUtil.jar -DgroupId=gpsUtil -DartifactId=gpsUtil -Dversion=1.0.0 -Dpackaging=jar &&     \
    mvn install:install-file -Dfile=libs/RewardCentral.jar -DgroupId=rewardCentral -DartifactId=rewardCentral -Dversion=1.0.0 -Dpackaging=jar &&     \
    mvn install:install-file -Dfile=libs/TripPricer.jar -DgroupId=tripPricer -DartifactId=tripPricer -Dversion=1.0.0 -Dpackaging=jar &&     \
    mvn clean package -DskipTests

# Étape 2 : exécution plus légère
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```



## Auteur

Projet réalisé, dans le cadre de la formation OpenClassRooms.

---



```
