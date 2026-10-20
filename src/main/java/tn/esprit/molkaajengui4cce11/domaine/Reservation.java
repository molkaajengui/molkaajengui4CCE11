package tn.esprit.molkaajengui4cce11.domaine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnoreProperties({"reservations", "maintenances", "equipements", "hibernateLazyInitializer", "handler"})
    private Vehicule vehicule;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnoreProperties({"reservations", "hibernateLazyInitializer", "handler"})
    private Client client;

    @OneToOne(mappedBy = "reservation")
    @ToString.Exclude
    @JsonIgnore
    private Contrat contrat;
}
