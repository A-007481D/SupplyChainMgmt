# Tricol Supply Management System

## Contexte du projet
Tricol est une entreprise spécialisée dans la conception et la fabrication de vêtements destinés aux professionnels. Les dirigeants souhaitent mettre en place une application pour gérer efficacement les approvisionnements de l'entreprise.

Cette première phase du projet se concentre sur **la gestion des fournisseurs**, posant les bases pour un système complet de gestion des approvisionnements incluant ultérieurement les produits, commandes et stocks.

L’objectif est de développer un module robuste de gestion des fournisseurs en utilisant les fondamentaux de **Spring Core**, avec une architecture extensible pour l’avenir.

---

## Fonctionnalités

### Gestion des fournisseurs
- **Ajouter un fournisseur** : Enregistrer un fournisseur avec les informations suivantes : société, adresse, contact, email, téléphone, ville, ICE (Identifiant Commun Entreprise).
- **Modifier un fournisseur** : Mettre à jour les informations d’un fournisseur existant.
- **Supprimer un fournisseur** : Retirer un fournisseur du système.
- **Consulter la liste des fournisseurs** : Afficher tous les fournisseurs avec tri par nom.

---

## Architecture et Technologies

### Technologie principale
- Java avec **Spring Core**
- **IoC Container** pour la gestion des dépendances
- Spring Beans et scopes de beans
- **ApplicationContext** et **BeanFactory**
- Configuration Spring avec **XML, annotations et Java Config**
- **Component Scanning**

### Spring MVC
- Conception en couches : **Repository → Service → Controller**
- Utilisation des interfaces repository de **Spring Data JPA**
- Implémentation des patterns **Service** et **Controller**
- Endpoints REST pour la gestion des fournisseurs

### Persistance
- **Spring Data JPA** pour la couche d'accès aux données
- Requêtes automatiques : `findAll()`, `findById()`, `count()`, etc.
- Requêtes personnalisées avec **Query Methods** : `findByNom(..)`, `findByEmailEndingWith(..)`, etc.

---

## Endpoints REST

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/v1/fournisseurs` | Récupérer tous les fournisseurs |
| GET | `/api/v1/fournisseurs/{id}` | Récupérer un fournisseur par son ID |
| POST | `/api/v1/fournisseurs` | Ajouter un nouveau fournisseur |
| PUT | `/api/v1/fournisseurs/{id}` | Mettre à jour un fournisseur existant |
| DELETE | `/api/v1/fournisseurs/{id}` | Supprimer un fournisseur |

### Endpoints de recherche
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/v1/fournisseurs/search/byNom?nom={nom}` | Chercher les fournisseurs par nom |
| GET | `/api/v1/fournisseurs/search/byEmailSuffix?suffix={suffix}` | Chercher les fournisseurs par suffixe d’email |

---

## Installation et exécution

### Prérequis
- JDK 17 ou supérieur
- Maven 3.8+
- PostgreSQL
- IDE (IntelliJ, Eclipse, VSCode, etc.)

### Configuration
1. Cloner le projet :
```bash
git clone <repo-url>
