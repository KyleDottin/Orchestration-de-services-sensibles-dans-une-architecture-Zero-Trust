# Orchestration de services sensibles dans une architecture Zero Trust

Cette plateforme *self-hosted* de gestion et de signature de documents confidentiels est conçue selon le modèle de sécurité **Zero Trust** (*"Never trust, always verify"*). Ce projet est un projet scolaire, il existe donc évidemment des failles et ne peut pas être utiliser tel qu'il est.
L'idée principale de ce projet vient du problème qui existe pour toutes les entreprises où aucun outil open source n'est de confiance lors de la modification de PDF ainsi que lors de la signature numérique de document confidentiel. Il y a toujours un intermédiaire pour faire ce type de tâche. Ainsi, ce projet s'inscrit dans cette démarche afin de trouver une solution local et open source pour faire ses tâches de manière sécurisée.


---

## Architecture du Système

Contrairement à une architecture sécurisée classique où la confiance est accordée une fois la passerelle (Gateway) franchie, cette implémentation Zero Trust considère le réseau interne comme potentiellement hostile. **Chaque microservice réauthentifie et revalide le jeton JWT de manière totalement indépendante.**

L'écosystème est découpé en 4 modules autonomes derrière un point d'entrée unique :

* **API Gateway (`api-gateway`)** : Point d'entrée unique du cluster. Elle gère le routage et effectue une première validation des jetons JWT.
* **Document Service (`document-service`)** : Gère le stockage temporaire, l'upload, la fusion (*merge*) et le découpage (*split*) de fichiers PDF via Apache PDFBox.
* **Signature Service (`signature-service`)** : Responsable de la signature numérique cryptographique et de la vérification d'intégrité des documents.
* **Audit Service (`audit-service`)** : Journal d'audit immuable (*append-only*). Il enregistre de manière cryptographique et persistante chaque action utilisateur (accès, signature, modification) à des fins de traçabilité.

---

## Stack Technique

* **Framework Principal** : Java / Spring Boot 3.x
* **Routage & Sécurité active** : Spring Cloud Gateway & Spring Security OAuth2 / Resource Server
* **Gestion d'Identité (IdP)** : Keycloak (OAuth2 / OIDC & JWT)
* **Traitement PDF** : Apache PDFBox
* **Persistance** : PostgreSQL
* **Conteneurisation** : Docker / Docker Compose

---

## Lancement de l'Application

### Prérequis
1. Disposer d'un environnement Java (JDK 17 ou supérieur).
2. Avoir démarré l'instance **Keycloak** au préalable (se référer au fichier `README-KEYCLOAK.md` dédié pour la configuration du Realm `1stRealm` et du client `Zerotrust-gateway`).

### Étape unique pour chaque service
Pour démarrer l'infrastructure, vous devez ouvrir un terminal dans le dossier de **chaque microservice** et exécuter la commande Spring Boot associée au wrapper Gradle (`gradlew`).

#### 1. Exemple pour le démarrage de l'API Gateway
```bash
cd api-gateway
./gradlew bootRun

```

## Auteurs

- **Kyle DOTTIN**
