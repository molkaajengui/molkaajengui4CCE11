# Atelier 5 : Spring MVC et SpringDoc — Exposer les services en API REST, tester avec Postman puis documenter avec Swagger
**Module :** Architecture des Systèmes d'Information (ASI)  
**Étudiante :** Molka Ajengui (4CCE11)  
**Projet :** AutoLoc API (`autoloc-api`)  
**Préfixe global de l'API :** `/api` (défini par `server.servlet.context-path=/api`)

---

## A. Endpoints

L'ensemble des endpoints respecte strictement les conventions REST (ressources au pluriel, verbes HTTP normalisés, contrôleurs minces injectés par constructeur via les interfaces de service `I...Service`).

| Contrôleur | Verbe HTTP et Route | Service appelé | Code de succès | Codes d'erreur | Description de l'opération |
| :--- | :--- | :--- | :---: | :---: | :--- |
| `ContratController` | `GET /api/contrats` | `contratService.findAll()` | **200 OK** | — | Lister tous les contrats de location |
| `ContratController` | `GET /api/contrats/{id}` | `contratService.findById(id)` | **200 OK** | **404** | Consulter un contrat par son identifiant |
| `ContratController` | `POST /api/contrats` | `contratService.create(contrat)` | **201 Created** | **400** | Créer un nouveau contrat (éventuellement avec ses paiements) |
| `ContratController` | `PUT /api/contrats/{id}` | `contratService.update(id, contrat)` | **200 OK** | **404, 400** | Modifier un contrat existant |
| `ContratController` | `DELETE /api/contrats/{id}` | `contratService.deleteById(id)` | **204 No Content** | **404** | Supprimer un contrat (cascade remove sur ses paiements) |
| `PaiementController` | `GET /api/paiements` | `paiementService.findAll()` | **200 OK** | — | Lister tous les paiements enregistrés (lecture seule) |
| `PaiementController` | `GET /api/paiements/{id}` | `paiementService.findById(id)` | **200 OK** | **404** | Consulter un paiement par son identifiant |
| `PaiementController` | `POST /api/paiements` | *(Non exposé — lecture seule)* | — | **405** | Refus de création directe d'un paiement orphelin |
| `AgenceController` | `GET /api/agences` | `agenceService.findAll()` | **200 OK** | — | Lister toutes les agences |
| `AgenceController` | `GET /api/agences/{id}` | `agenceService.findById(id)` | **200 OK** | **404** | Consulter une agence par son identifiant |
| `AgenceController` | `POST /api/agences` | `agenceService.create(agence)` | **201 Created** | **400** | Créer une agence (nom obligatoire non vide) |
| `AgenceController` | `PUT /api/agences/{id}` | `agenceService.update(id, agence)` | **200 OK** | **404, 400** | Modifier les informations d'une agence |
| `AgenceController` | `DELETE /api/agences/{id}` | `agenceService.deleteById(id)` | **204 No Content** | **404** | Supprimer une agence par son identifiant |
| `EmployeController` | `GET /api/employes` | `employeService.findAll()` | **200 OK** | — | Lister tous les employés |
| `EmployeController` | `GET /api/employes/{id}` | `employeService.findById(id)` | **200 OK** | **404** | Consulter un employé par son identifiant |
| `EmployeController` | `POST /api/employes` | `employeService.create(employe)` | **201 Created** | **400** | Créer un employé (rattachement agence imbriquée `{ "agence": { "idAgence": 1 } }`) |
| `EmployeController` | `PUT /api/employes/{id}` | `employeService.update(id, employe)` | **200 OK** | **404, 400** | Mettre à jour un employé |
| `EmployeController` | `DELETE /api/employes/{id}` | `employeService.deleteById(id)` | **204 No Content** | **404** | Supprimer un employé par son identifiant |
| `VehiculeController` | `GET /api/vehicules` | `vehiculeService.findAll()` | **200 OK** | — | Lister tous les véhicules du parc |
| `VehiculeController` | `GET /api/vehicules/{id}` | `vehiculeService.findById(id)` | **200 OK** | **404** | Consulter un véhicule par son identifiant |
| `VehiculeController` | `POST /api/vehicules` | `vehiculeService.create(vehicule)` | **201 Created** | **400** | Ajouter un véhicule (rejet si tarif journalier négatif) |
| `VehiculeController` | `PUT /api/vehicules/{id}` | `vehiculeService.update(id, vehicule)` | **200 OK** | **404, 400** | Modifier les caractéristiques d'un véhicule |
| `VehiculeController` | `DELETE /api/vehicules/{id}` | `vehiculeService.deleteById(id)` | **204 No Content** | **404** | Supprimer un véhicule du parc |
| `EquipementController` | `GET /api/equipements` | `equipementService.findAll()` | **200 OK** | — | Lister tous les équipements disponibles |
| `EquipementController` | `GET /api/equipements/{id}` | `equipementService.findById(id)` | **200 OK** | **404** | Consulter un équipement par son identifiant |
| `EquipementController` | `POST /api/equipements` | `equipementService.create(equipement)` | **201 Created** | **400** | Créer un équipement (libellé obligatoire non vide) |
| `EquipementController` | `PUT /api/equipements/{id}` | `equipementService.update(id, equipement)` | **200 OK** | **404, 400** | Modifier un équipement existant |
| `EquipementController` | `DELETE /api/equipements/{id}` | `equipementService.deleteById(id)` | **204 No Content** | **404** | Supprimer un équipement par son identifiant |
| `ClientController` | `GET /api/clients` | `clientService.findAll()` | **200 OK** | — | Lister tous les clients |
| `ClientController` | `GET /api/clients/{id}` | `clientService.findById(id)` | **200 OK** | **404** | Consulter un client par son identifiant |
| `ClientController` | `POST /api/clients` | `clientService.create(client)` | **201 Created** | **400** | Créer un client (nom et e-mail obligatoires) |
| `ClientController` | `PUT /api/clients/{id}` | `clientService.update(id, client)` | **200 OK** | **404, 400** | Mettre à jour un client |
| `ClientController` | `DELETE /api/clients/{id}` | `clientService.deleteById(id)` | **204 No Content** | **404** | Supprimer un compte client |
| `ReservationController` | `GET /api/reservations` | `reservationService.findAll()` | **200 OK** | — | Lister toutes les réservations |
| `ReservationController` | `GET /api/reservations/{id}` | `reservationService.findById(id)` | **200 OK** | **404** | Consulter une réservation par son identifiant |
| `ReservationController` | `POST /api/reservations` | `reservationService.create(reservation)` | **201 Created** | **400** | Créer une réservation (`client` et `vehicule` imbriqués, date fin >= début) |
| `ReservationController` | `PUT /api/reservations/{id}` | `reservationService.update(id, reservation)` | **200 OK** | **404, 400** | Mettre à jour une réservation |
| `ReservationController` | `DELETE /api/reservations/{id}` | `reservationService.deleteById(id)` | **204 No Content** | **404** | Supprimer une réservation |
| `MaintenanceController` | `GET /api/maintenances` | `maintenanceService.findAll()` | **200 OK** | — | Lister toutes les opérations de maintenance |
| `MaintenanceController` | `GET /api/maintenances/{id}` | `maintenanceService.findById(id)` | **200 OK** | **404** | Consulter une maintenance par son identifiant |
| `MaintenanceController` | `POST /api/maintenances` | `maintenanceService.create(maintenance)` | **201 Created** | **400** | Déclarer une maintenance (`vehicule` imbriqué, date fin >= début) |
| `MaintenanceController` | `PUT /api/maintenances/{id}` | `maintenanceService.update(id, maintenance)` | **200 OK** | **404, 400** | Modifier une fiche de maintenance |
| `MaintenanceController` | `DELETE /api/maintenances/{id}` | `maintenanceService.deleteById(id)` | **204 No Content** | **404** | Supprimer une opération de maintenance |

---

## B. Annotations Jackson

Dans le cadre de l'exposition directe des entités JPA, les relations bidirectionnelles provoquent par défaut des boucles infinies de sérialisation (`StackOverflowError` de Jackson). Deux stratégies complémentaires ont été appliquées selon la sémantique de l'association :

### 1. Tableau des annotations retenues par association

| Association | Placement de l'annotation | Annotation retenue | Justification technique et métier |
| :--- | :--- | :--- | :--- |
| **`Contrat` ↔ `Paiement`** | Côté parent (`Contrat.paiements`) :<br>`@JsonManagedReference("contrat-paiements")`<br><br>Côté enfant (`Paiement.contrat`) :<br>`@JsonBackReference("contrat-paiements")` | Paire `@JsonManagedReference` / `@JsonBackReference` | **Composition forte** avec `cascade = ALL` et `orphanRemoval = true`. L'enfant `Paiement` est créé directement à l'intérieur du JSON de son contrat parent. Jackson sérialise la collection des paiements dans la réponse du contrat, mais brise la boucle en omettant le champ parent dans chaque paiement sérialisé. À la désérialisation, Jackson rétablit automatiquement le lien parent-enfant en mémoire. |
| **`Agence` ↔ `Vehicule`** | Côté parent (`Agence.vehicules`) :<br>`@JsonIgnore`<br><br>Côté enfant (`Vehicule.agence`) :<br>Aucune annotation d'exclusion | `@JsonIgnore` sur la collection inverse | **Agrégation simple**. Les véhicules ont un cycle de vie autonome et sont créés séparément via `/api/vehicules` en fournissant la référence parent `{ "agence": { "idAgence": 1 } }`. Sérialiser tous les véhicules à chaque consultation d'agence alourdirait la réponse et générerait des cycles inutiles. |
| **`Agence` ↔ `Employe`** | `Agence.employes` : `@JsonIgnore` | `@JsonIgnore` | Mêmes motifs : l'employé est géré de façon autonome et référence son agence par son identifiant. |
| **`Client` ↔ `Reservation`** | `Client.reservations` : `@JsonIgnore` | `@JsonIgnore` | Les réservations d'un client sont créées via `/api/reservations` en ciblant le client par identifiant. |
| **`Vehicule` ↔ `Maintenance`** | `Vehicule.maintenances` : `@JsonIgnore` | `@JsonIgnore` | La maintenance est déclarée via son propre contrôleur et désigne le véhicule concerné par identifiant. |
| **`Vehicule` ↔ `Reservation`** | `Vehicule.reservations` : `@JsonIgnore` | `@JsonIgnore` | Évite le chargement superflu et les cycles infinis lors de l'accès aux véhicules. |
| **`Vehicule` ↔ `Equipement`** | `Equipement.vehicules` : `@JsonIgnore` | `@JsonIgnore` | Relation ManyToMany où la consultation d'un équipement ne doit pas lister récursivement tout le parc de véhicules associés. |
| **`Reservation` ↔ `Contrat`** | `Reservation.contrat` : `@JsonIgnore` | `@JsonIgnore` | Association OneToOne : la réservation est consultable indépendamment sans boucle vers le contrat. |

### 2. Différence fondamentale entre `@JsonBackReference` et `@JsonIgnore`

- **`@JsonBackReference`** :
  - Fonctionne impérativement en tandem avec `@JsonManagedReference` portant le même nom.
  - Elle informe Jackson qu'il s'agit du lien retour (inverse) vers l'entité parente.
  - **À la sérialisation (écriture JSON) :** le champ est masqué pour stopper la récursion.
  - **À la désérialisation (lecture JSON) :** Jackson **reconstruit automatiquement la référence bidirectionnelle** dans l'objet enfant en lui assignant l'instance du parent en cours de désérialisation.
- **`@JsonIgnore`** :
  - Bloque complètement la sérialisation et la désérialisation du champ sans logique de rétablissement de relation.
  - Placer par erreur `@JsonBackReference` sur `Reservation.client` conduirait à ignorer le client lors d'une requête POST, créant une réservation orpheline sans client. `@JsonIgnore` est donc réservé aux collections inverses dans les relations d'agrégation où l'enfant est créé seul.

---

## C. Postman et Swagger

| Critère | Postman | Swagger UI (SpringDoc OpenAPI) |
| :--- | :--- | :--- |
| **Rôle principal** | **Client de test dynamique et automatisable** | **Spécification vivante, documentation et exploration** |
| **Fonctionnement** | Envoie des requêtes HTTP réelles vers l'API, exécute des assertions JavaScript (`pm.test`, `pm.expect`), mémorise des variables d'environnement (`pm.environment.set`), et permet de construire des suites de tests automatisées rejouables. | Analyse le code source Java (`@RestController`, `@Tag`, `@Operation`, annotations OpenAPI) au démarrage de l'application et génère dynamiquement le schéma OpenAPI v3 (JSON sous `/api/v3/api-docs` et interface web sous `/api/swagger-ui.html`). |
| **Exemple concret rencontré dans l'atelier** | **Test de cascade Contrat ↔ Paiement :** Dans Postman, nous avons exécuté la création d'un contrat complet avec 2 paiements imbriqués (`POST /api/contrats`). Le script *After response* a automatiquement extrait l'identifiant généré via `pm.environment.set("contratId", pm.response.json().idContrat)`. Ce paramètre dynamique a immédiatement servi à tester la modification (`PUT /api/contrats/{{contratId}}`), puis la suppression (`DELETE /api/contrats/{{contratId}}` renvoyant 204), avant de vérifier que `GET /api/paiements/{idPaiement}` renvoyait bien un 404 (suppression en cascade `orphanRemoval/REMOVE`). | **Exploration interactive et validation du contrat d'interface :** Depuis Swagger UI, nous avons visualisé directement les groupes documentés (*Contrats*, *Paiements*, *Agences*, etc.), examiné les schémas JSON attendus déduits automatiquement des entités, et validé immédiatement les réponses documentées (ex. code 404 pour `GET /api/contrats/{id}` et code 400 pour données invalides) via le bouton *Try it out / Execute* sans avoir à préparer manuellement des payloads dans un client lourd. |

---

## D. Anomalies SonarQube for IDE

L'analyse statique du code de la couche web (`web.controller`) et du domaine a permis de relever et corriger les anomalies suivantes :

| Règle SonarQube | Type & Sévérité | Explication de l'anomalie | Correction apportée |
| :--- | :--- | :--- | :--- |
| **S6813** (*Field dependency injection should not be used in Spring components*) | Code Smell (Majeur) | L'utilisation de `@Autowired` sur des champs privés dans les contrôleurs masque les dépendances obligatoires, empêche de déclarer les champs `final` (perte d'immutabilité), et interdit l'instanciation manuelle propre avec `new` dans les tests unitaires. | Utilisation systématique de champs déclarés `private final I...Service` et génération du constructeur requis via l'annotation Lombok `@RequiredArgsConstructor` sur tous les contrôleurs REST. |
| **S1128 / Nettoyage d'imports et code mort** | Code Smell (Mineur) | La présence d'imports superflus ou inutilisés (ex. imports résiduels de bibliothèques tierces, annotations non référencées) pollue le code source et augmente la dette technique. | Élimination de tous les imports inutiles et optimisation des dépendances dans `ContratController`, `AgenceController`, `VehiculeController`, etc. |
| **Architecture en couches & contrôleurs minces** | Code Smell (Critique) | Les contrôleurs ne doivent en aucun cas accéder directement aux interfaces de persistance (`...Repository`) ni implémenter de logique de validation métier ou de calcul de persistance. | Découplage strict : les 9 contrôleurs dépendent uniquement et exclusivement des interfaces de service métier (`I...Service`), sans aucun accès direct aux repositories Spring Data JPA. |

---

## E. Questions de compréhension

### 1. Quel composant reçoit toutes les requêtes, et comment retrouve-t-il la méthode du contrôleur à appeler ?
Toutes les requêtes HTTP entrantes sont interceptées par le **`DispatcherServlet`**, qui joue le rôle de *Front Controller* central de Spring MVC.  
Pour trouver la méthode à exécuter, le `DispatcherServlet` consulte le composant **`HandlerMapping`** (plus précisément `RequestMappingHandlerMapping`). Ce dernier maintient en mémoire une table de correspondance construite au démarrage de l'application en inspectant les annotations `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc. En comparant l'URL demandée, la méthode HTTP (GET, POST, etc.) et les en-têtes de la requête, le `HandlerMapping` retourne un `HandlerExecutionChain` désignant la méthode précise du contrôleur à invoquer via un `HandlerAdapter`.

### 2. Pourquoi `ContratController` dépend-il de `IContratService` et non de `ContratRepository` ni de `ContratServiceImpl` ?
- **Non-dépendance envers `ContratRepository` :** Pour préserver le principe de séparation des responsabilités et l'architecture en couches. Le contrôleur web est un contrôleur mince responsable uniquement de la gestion HTTP (désérialisation, routage, codes de retour) ; toute la logique métier, la validation et les transactions doivent être isolées dans la couche Service.
- **Non-dépendance envers `ContratServiceImpl` :** Pour respecter le principe SOLID d'**inversion des dépendances (DIP)** : les modules de haut niveau doivent dépendre d'abstractions et non d'implémentations concrètes. S'appuyer sur l'interface `IContratService` assure un découplage fort, permet à Spring de créer des proxys dynamiques transparents (gestion des transactions `@Transactional`, sécurité) et facilite l'injection de bouchons (*mocks*) lors des tests.

### 3. Pourquoi un POST renvoie-t-il 201 et un DELETE 204 ? Pourquoi une ressource absente donne-t-elle 404 et non 500 ?
- **POST renvoie 201 (Created) :** Selon la norme HTTP/1.1 (RFC 7231), le statut `201 Created` indique que la requête a abouti à la création effective d'une nouvelle ressource sur le serveur.
- **DELETE renvoie 204 (No Content) :** Le statut `204 No Content` signifie que l'action de suppression a été accomplie avec succès sur le serveur et qu'il n'y a plus aucun contenu à renvoyer dans le corps de la réponse.
- **Ressource absente renvoie 404 (Not Found) et non 500 (Internal Server Error) :** Le code HTTP `404` est une erreur client signalant que la ressource demandée (URI ou identifiant) n'existe pas, ce qui constitue une issue normale du protocole REST. Le code `500` est réservé aux pannes imprévues et anomalies internes du serveur (bug non intercepté, échec matériel, rupture de base de données). L'exception `ResourceNotFoundException` levée par le service est donc traduite en statut 404 par le `GlobalExceptionHandler`.

### 4. Pourquoi `PaiementController` n'expose-t-il que des GET ? Quelle réponse reçoit un POST `/api/paiements` ?
- `PaiementController` n'expose que des méthodes de lecture (`findAll` et `findById`) car un paiement relève d'une **composition stricte** avec son `Contrat`. Un paiement ne peut avoir de sens ni de cycle de vie autonome : il est créé, modifié ou supprimé exclusivement à travers le contrat de location qui le porte (via les cascades JPA `CascadeType.ALL` et `orphanRemoval = true`). Interdire les modifications directes sur `/api/paiements` garantit l'intégrité financière et relationnelle du domaine.
- Un client qui envoie une requête `POST /api/paiements` reçoit une réponse HTTP **`405 Method Not Allowed`**, car le contrôleur ne définit aucun `@PostMapping` pour cette route.

### 5. Quels risques présente l'exposition directe des entités JPA ? Pourquoi `Contrat.paiements` utilise-t-il `@JsonManagedReference` alors que `Agence.vehicules` utilise `@JsonIgnore` ?
- **Risques de l'exposition directe des entités JPA :**
  1. *Couplage excessif :* Le contrat d'API publique devient dépendant de la structure interne de la base de données.
  2. *Fuite de données et vulnérabilités :* Risque d'exposition de données confidentielles ou internes, et risque d'attaque par assignation massive (*over-posting / mass assignment*) où un client peut modifier des attributs non prévus.
  3. *Problèmes de sérialisation :* Erreurs de chargement paresseux (`LazyInitializationException` ou proxys `ByteBuddyInterceptor`), et requêtes SQL redondantes (problème du N+1). Ces risques sont résolus dans le Chapitre 10 par l'introduction des DTO (*Data Transfer Objects*).
- **Pourquoi `Contrat.paiements` utilise `@JsonManagedReference` :** Car c'est une relation de **composition**. Les paiements sont créés directement dans le corps JSON du contrat parent lors d'un `POST /api/contrats`. `@JsonManagedReference` permet à Jackson d'inclure les paiements dans le JSON tout en résolvant la référence inverse sans boucle infinie.
- **Pourquoi `Agence.vehicules` utilise `@JsonIgnore` :** Car c'est une relation d'**agrégation**. Les véhicules sont créés indépendamment via leur propre endpoint (`POST /api/vehicules`) en désignant l'agence par son identifiant `{ "agence": { "idAgence": 1 } }`. Exposer la liste de tous les véhicules dans chaque agence alourdirait massivement les payloads et n'est pas nécessaire aux opérations sur les agences.
