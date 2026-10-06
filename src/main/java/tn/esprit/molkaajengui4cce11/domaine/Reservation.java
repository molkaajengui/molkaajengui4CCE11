package tn.esprit.molkaajengui4cce11.domaine;

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
    private Vehicule vehicule;

    @ManyToOne
    @ToString.Exclude
    private Client client;

    @OneToOne(mappedBy = "reservation")
    @ToString.Exclude
    private Contrat contrat;
}
