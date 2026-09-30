package tn.esprit.molkaajengui4cce11.domaine;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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
}
