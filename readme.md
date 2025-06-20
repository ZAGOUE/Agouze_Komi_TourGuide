# Technologies

> Java 17  
> Spring Boot 3.X  
> JUnit 5  

# How to have gpsUtil, rewardCentral and tripPricer dependencies available ?

> Run : 
- mvn install:install-file -Dfile=/libs/gpsUtil.jar -DgroupId=gpsUtil -DartifactId=gpsUtil -Dversion=1.0.0 -Dpackaging=jar  
- mvn install:install-file -Dfile=/libs/RewardCentral.jar -DgroupId=rewardCentral -DartifactId=rewardCentral -Dversion=1.0.0 -Dpackaging=jar  
- mvn install:install-file -Dfile=/libs/TripPricer.jar -DgroupId=tripPricer -DartifactId=tripPricer -Dversion=1.0.0 -Dpackaging=jar




## 🧱 Architecture

```

Projet\_8/
├── .github/workflows/ci.yml       # Pipeline CI/CD
├── TourGuide/
│   ├── src/
│   │   ├── main/java              # Code source
│   │   └── test/java              # Tests unitaires et de charge
│   ├── pom.xml                    # Projet Maven
│   ├── README.md                  # Documentation du projet
│   └── TESTING.md                 # Détail des tests effectués

````

---

## 🚀 Fonctionnalités clés

- Suivi de la position des utilisateurs (simulation `gpsUtil`)
- Attribution de récompenses automatiques (via `RewardsCentral`)
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
TourGuide/target/site/jacoco/index.html
```

---

## Tests de performance

> Le test `TestPerformance.java` est conçu pour évaluer la scalabilité du système.
> Il est **exclu du cycle CI classique** pour ne pas ralentir l'intégration continue.

---

## CI/CD – GitHub Actions

Le pipeline CI se déclenche automatiquement :

* À chaque `push` ou `pull_request` sur la branche `master`
* Il compile le projet, exécute les tests et archive :

    * Le `.jar` final
    * Le rapport de couverture JaCoCo

---



## Auteur

Projet réalisé, dans le cadre de la formation OpenClassRooms.

---



```
