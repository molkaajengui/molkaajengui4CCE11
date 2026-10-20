package tn.esprit.molkaajengui4cce11.domaine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehicule;

    private String immatriculation;
    private String marque;
    private String modele;

    @Enumerated(EnumType.STRING)
    private CategorieVehicule categorie;

    private BigDecimal tarifJournalier;

    @Enumerated(EnumType.STRING)
    private StatutVehicule statut;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnoreProperties({"employes", "vehicules", "hibernateLazyInitializer", "handler"})
    private Agence agence;

    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore
    private List<Maintenance> maintenances = new ArrayList<>();

    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore
    private List<Reservation> reservations = new ArrayList<>();

    @ManyToMany
    @ToString.Exclude
    @Builder.Default
    @JsonIgnoreProperties({"vehicules", "hibernateLazyInitializer", "handler"})
    private List<Equipement> equipements = new ArrayList<>();

    public void addMaintenance(Maintenance maintenance) {
        if (maintenances == null) {
            maintenances = new ArrayList<>();
        }
        maintenances.add(maintenance);
        maintenance.setVehicule(this);
    }

    public void removeMaintenance(Maintenance maintenance) {
        if (maintenances != null) {
            maintenances.remove(maintenance);
            maintenance.setVehicule(null);
        }
    }

    public void addReservation(Reservation reservation) {
        if (reservations == null) {
            reservations = new ArrayList<>();
        }
        reservations.add(reservation);
        reservation.setVehicule(this);
    }

    public void removeReservation(Reservation reservation) {
        if (reservations != null) {
            reservations.remove(reservation);
            reservation.setVehicule(null);
        }
    }
}
