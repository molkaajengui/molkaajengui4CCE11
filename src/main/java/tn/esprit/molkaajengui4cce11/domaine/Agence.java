package tn.esprit.molkaajengui4cce11.domaine;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgence;

    private String nom;
    private String ville;
    private String adresse;
    private String telephone;

    @OneToMany(mappedBy = "agence", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private List<Employe> employes = new ArrayList<>();

    @OneToMany(mappedBy = "agence", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private List<Vehicule> vehicules = new ArrayList<>();

    // Méthodes d'aide (Helper methods) pour la relation Parent-Enfant
    public void addEmploye(Employe employe) {
        if (employes == null) {
            employes = new ArrayList<>();
        }
        employes.add(employe);
        employe.setAgence(this);
    }

    public void removeEmploye(Employe employe) {
        if (employes != null) {
            employes.remove(employe);
            employe.setAgence(null);
        }
    }

    public void addVehicule(Vehicule vehicule) {
        if (vehicules == null) {
            vehicules = new ArrayList<>();
        }
        vehicules.add(vehicule);
        vehicule.setAgence(this);
    }

    public void removeVehicule(Vehicule vehicule) {
        if (vehicules != null) {
            vehicules.remove(vehicule);
            vehicule.setAgence(null);
        }
    }
}
