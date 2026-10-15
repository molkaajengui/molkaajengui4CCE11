# Atelier 4 : Inversion de contrôle, injection de dépendances et couche Service
**Module :** Architecture des Systèmes d'Information (ASI)  
**Étudiante :** Molka Ajengui (4CCE11)  
**Projet :** AutoLoc API (`autoloc-api`)

---

## A. Modes d'injection comparés

Ce tableau synthétise la comparaison entre les deux principaux modes d'injection étudiés : l'injection par constructeur (bonne pratique adoptée sur tout le projet AutoLoc) et l'injection par attribut via `@Autowired`.

| Critère | Injection par constructeur | Injection par attribut (`@Autowired`) |
| :--- | :--- | :--- |
| **Champ `final` possible ?** | **Oui**. Les dépendances sont initialisées à l'instanciation dans le constructeur, garantissant l'immutabilité du bean. | **Non**. Les champs ne peuvent pas être `final` car Spring les injecte par réflexion après l'appel au constructeur. |
| **Dépendance visible dans la signature ?** | **Oui**. Toutes les dépendances obligatoires apparaissent explicitement dans la liste des paramètres du constructeur. | **Non**. Les dépendances sont masquées à l'intérieur de la classe et invisibles de l'extérieur. |
| **Utilisable avec `new` hors conteneur sans dépendance `null` ?** | **Oui**. La classe peut être instanciée directement avec `new` dans des tests unitaires en fournissant des mocks ou stubs. | **Non**. Instancier la classe hors conteneur avec son constructeur par défaut laisse les dépendances à `null` (`NullPointerException` garanti). |
| **Signalé par SonarQube for IDE ?** | **Non**. Recommandé par SonarQube et l'équipe Spring comme bonne pratique Clean Code. | **Oui**. Déclenche la règle de sévérité majeure **S6813** (*"Field dependency injection should not be used in Spring components. Prefer constructor injection"*). |

---

## B. Services

Tous les services de l'application AutoLoc respectent la convention multicouche :
- Interface `I...Service` et classe concrète `...ServiceImpl` dans le package `service`.
- Injection par constructeur via l'annotation Lombok `@RequiredArgsConstructor` et des champs `final`.
- Dépendances typées par abstraction (interfaces `I...Repository`).
- Services sans état (*stateless singletons*).

| Service | Dépendances injectées | Mode d'injection | Justification |
| :--- | :--- | :--- | :--- |
| `ContratServiceImpl` | `IContratRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire immuable (`final`), découplage par interface, permet le test unitaire sans conteneur Spring. |
| `AgenceServiceImpl` | `IAgenceRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire immuable (`final`), conformité SonarQube S6813, garantit la complétude de l'objet dès l'instanciation. |
| `EmployeServiceImpl` | `IEmployeRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire immuable (`final`), assure la présence du repository dès la création du bean singleton. |
| `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire immuable (`final`), abstraction complète de l'accès aux données, service sans état. |
| `EquipementServiceImpl` | `IEquipementRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire immuable (`final`), respect des conventions Clean Code et élimination des injections cachées. |
| `ClientServiceImpl` | `IClientRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire immuable (`final`), respect du principe SOLID d'inversion des dépendances (DIP). |
| `ReservationServiceImpl` | `IReservationRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire immuable (`final`), permet l'injection de mocks lors des tests d'intégration ou unitaires. |
| `PaiementServiceImpl` | `IPaiementRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire immuable (`final`), service dédié à la consultation seule (lecture seule), intégrité garantie. |
| `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire immuable (`final`), composant sans état, injection automatique par Spring grâce au constructeur unique. |

---

## C. Messages d'erreur du conteneur Spring

### Message 1
```text
APPLICATION FAILED TO START
Description:
Parameter 0 of constructor in tn.esprit.autoloc.web.controller.ContratController
required a bean of type 'tn.esprit.autoloc.service.IContratService' that could not be found.
Action:
Consider defining a bean of type 'tn.esprit.autoloc.service.IContratService' in your configuration.
```
- **Type d'exception :** `NoSuchBeanDefinitionException` (enveloppée dans une `UnsatisfiedDependencyException`).
- **Bean concerné :** `IContratService` (requis par `ContratController`).
- **Deux causes les plus probables côté `ContratServiceImpl` :**
  1. *Omission de l'annotation de stéréotype :* L'annotation `@Service` (ou `@Component`) a été oubliée au-dessus de la classe `ContratServiceImpl`. Spring ne la reconnaît donc pas comme un composant managé.
  2. *Package hors du scan :* La classe `ContratServiceImpl` est placée dans un package qui n'est pas scanné par `@SpringBootApplication` (par exemple en dehors du package racine ou de ses sous-packages).
- **Correction :**
  Ajouter l'annotation `@Service` sur `ContratServiceImpl` et s'assurer que la classe est bien située dans le sous-package `service` sous le package racine de l'application.

---

### Message 2
```text
Description:
Parameter 0 of constructor in tn.esprit.autoloc.web.controller.NotificationController
required a single bean, but 2 were found:
 - emailNotificateur
 - smsNotificateur
Action:
Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans,
or using @Qualifier to identify the bean that should be consumed
```
- **Type d'exception :** `NoUniqueBeanDefinitionException`.
- **Bean concerné :** Dépendance de type `INotificateur` injectée dans `NotificationController`.
- **Cause :** Deux beans différents (`emailNotificateur` et `smsNotificateur`) implémentent la même interface demandée. Spring ne peut pas décider arbitrairement lequel injecter.
- **Deux propositions de correction :**
  1. **Utiliser `@Primary` :** Placer `@Primary` sur la classe de l'implémentation privilégiée par défaut (sur la classe du bean fournisseur) :
     ```java
     @Service
     @Primary
     public class EmailNotificateur implements INotificateur { ... }
     ```
  2. **Utiliser `@Qualifier` :** Placer `@Qualifier("nomDuBean")` directement au point d'injection (sur le paramètre du constructeur du consommateur) pour lever explicitement l'ambiguïté :
     ```java
     @RestController
     public class NotificationController {
         private final INotificateur notificateur;

         public NotificationController(@Qualifier("emailNotificateur") INotificateur notificateur) {
             this.notificateur = notificateur;
         }
     }
     ```

---

### Message 3
```text
The dependencies of some of the beans in the application context form a cycle:
 clientServiceImpl
 | ^
 v |
 reservationServiceImpl
```
- **Type d'exception :** `BeanCurrentlyInCreationException` (dépendance circulaire).
- **Pourquoi l'injection par constructeur rend-elle ce cycle impossible à contourner ?**
  Pour créer l'instance de `ClientServiceImpl`, Spring doit d'abord résoudre et fournir tous les arguments de son constructeur, dont `ReservationServiceImpl`. Mais pour instancier `ReservationServiceImpl`, Spring doit fournir `ClientServiceImpl` qui n'a pas encore fini d'être construit. Contrairement à l'injection par setter ou attribut où Spring peut créer des instances vides non initialisées puis faire les liens après, l'injection par constructeur impose qu'un objet soit totalement et validement construit dès son instanciation. Ce cycle provoque donc un interblocage d'instanciation irréversible.
- **Piste de conception pour supprimer ce cycle :**
  1. *Refactorisation et responsabilité commune :* Extraire la logique croisée dans un troisième service médiateur ou coordinateur (ex: `ReservationCoordinationService`) qui dépend des deux repositories ou services, supprimant ainsi la dépendance directe entre `ClientServiceImpl` et `ReservationServiceImpl`.
  2. *Dépendre des repositories plutôt que des services :* Faire en sorte que `ClientServiceImpl` injecte directement `IReservationRepository` (et inversement) sans passer par le service de l'autre entité.
  3. *Architecture événementielle :* Découpler les interactions en utilisant des événements applicatifs Spring (`ApplicationEventPublisher` et `@EventListener`).

---

## D. Anomalies SonarQube for IDE

| Règle SonarQube | Explication de l'anomalie | Correction apportée |
| :--- | :--- | :--- |
| **S6813** (*Field dependency injection should not be used in Spring components*) | L'injection par attribut (`@Autowired` sur champ privé) masque les dépendances, empêche de déclarer les champs `final` et provoque des `NullPointerException` si la classe est instanciée avec `new` hors conteneur (ex. en test unitaire). | Remplacement par une injection par constructeur avec des champs `final` et l'annotation Lombok `@RequiredArgsConstructor`. |
| **S112** (*Generic exceptions should never be thrown*) | Lever une exception trop générique telle que `RuntimeException` ou `Exception` empêche les appelants (contrôleurs, handlers globaux `@ControllerAdvice`) d'identifier précisément l'erreur métier et d'adapter le code HTTP (ex: 404 vs 500). | Création et utilisation d'une exception métier dédiée : `ResourceNotFoundException extends RuntimeException`, paramétrée avec le nom de la ressource et l'ID manquant. |
| **S1118 / Nettoyage de code mort et imports inutilisés** | La présence de classes non utilisées, d'imports superflus ou de conventions non respectées pollue le code et augmente la dette technique. De même, l'usage de `@Data` sur les entités JPA est proscrit car il surcharge `equals`/`hashCode`/`toString` de manière incompatible avec les proxys Hibernate. | Nettoyage des imports inutiles, adoption d'annotations Lombok ciblées (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`, `@ToString(exclude = ...)`), et suppression de tout code mort. |

---

## E. Questions de compréhension

### 1. Où est le `new` de `ContratServiceImpl` et qui l'exécute ?
Il n'y a aucun appel `new ContratServiceImpl(...)` écrit dans le code source de l'application. C'est le **conteneur Spring IoC** (`ApplicationContext` / `BeanFactory`) qui instancie la classe par réflexion Java au démarrage de l'application, après avoir détecté l'annotation `@Service` lors du Component Scan.

### 2. Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?
Pour respecter le principe d'**inversion des dépendances (DIP)** des principes SOLID : les modules de haut niveau ne doivent pas dépendre des modules de bas niveau, mais d'abstractions. Le contrôleur s'appuie sur le contrat d'interface sans être couplé aux détails d'implémentation. Cela autorise le remplacement de l'implémentation sans modifier le contrôleur, permet à Spring d'insérer des proxys dynamiques (transactions, sécurité) et simplifie grandement les tests unitaires via des mocks.

### 3. Pourquoi un service singleton doit-il rester sans état ? Donner un exemple de bug.
Dans Spring, le bean de service est par défaut un singleton partagé par tous les threads de l'application (chaque requête HTTP entrante est exécutée dans un thread distinct).  
Si le service conservait un état mutable (par exemple un attribut `private Contrat contratEnCours;`), une requête pour un client A pourrait écraser `contratEnCours` pendant qu'un thread pour un client B est en train d'enregistrer des paiements. Les données du client B seraient alors attribuées par erreur au contrat du client A (condition de course / *race condition* et corruption critique des données).

### 4. Pourquoi `update` charge-t-il l'entité existante avant d'appeler `save` au lieu de lui passer directement l'objet reçu ?
Appeler directement `save(contratRecu)` déclenche une opération `merge()` sur un objet détaché. Si l'objet reçu ne contient pas tous les attributs (champs omis ou non modifiables), ces derniers seraient écrasés par des valeurs `null`. De plus, pour les collections gérées en cascade avec `orphanRemoval = true` (comme la liste des paiements d'un contrat), passer un objet reçu avec une liste de paiements vide ou non initialisée supprimerait physiquement tous les paiements existants en base de données. Charger l'entité managée existante permet de recopier uniquement les attributs autorisés à la modification tout en préservant intactes les relations et l'intégrité de la persistance.

### 5. Différences `@Component` vs `@Bean`, `@Primary` vs `@Qualifier`, et spécificité de `IPaiementService`
- **Différence entre `@Component` et `@Bean` :**
  - `@Component` est une annotation de niveau classe appliquée directement sur le code source de nos propres classes pour en faire un bean scanné automatiquement.
  - `@Bean` est une annotation de méthode utilisée à l'intérieur d'une classe annotée `@Configuration`. Elle permet de déclarer manuellement un bean, indispensable pour instancier et configurer des classes provenant de bibliothèques tierces ou du JDK dont on ne peut pas modifier le code source.
- **Différence entre `@Primary` et `@Qualifier` :**
  - `@Primary` est positionné sur la classe du bean pour lui conférer une priorité par défaut si plusieurs beans du même type existent.
  - `@Qualifier("nomDuBean")` est positionné au point d'injection chez le consommateur pour spécifier expressément quel bean précis doit être injecté.
- **Pourquoi `IPaiementService` n'expose ni création, ni modification, ni suppression ?**
  Selon les règles métier et la relation parent-enfant modélisée dans l'application, le cycle de vie d'un paiement dépend entièrement de son `Contrat`. La création, la modification et la suppression d'un paiement se font exclusivement à travers le contrat parent (via les cascades JPA `CascadeType.ALL` et `orphanRemoval`). `IPaiementService` est donc volontairement restreint à la lecture seule (`findById`, `findAll`) afin d'empêcher toute manipulation orpheline incohérente d'un paiement sans son contrat.
