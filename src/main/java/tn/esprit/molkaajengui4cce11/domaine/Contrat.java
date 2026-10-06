package tn.esprit.molkaajengui4cce11.domaine;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContrat;

    private LocalDate dateSignature;
    private BigDecimal montantTotal;
    private boolean valide;

    @OneToOne
    @ToString.Exclude
    private Reservation reservation;

    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private List<Paiement> paiements = new ArrayList<>();

    // Méthodes d'aide (Helper methods) pour la relation Parent-Enfant
    public void addPaiement(Paiement paiement) {
        if (paiements == null) {
            paiements = new ArrayList<>();
        }
        paiements.add(paiement);
        paiement.setContrat(this);
    }

    public void removePaiement(Paiement paiement) {
        if (paiements != null) {
            paiements.remove(paiement);
            paiement.setContrat(null);
        }
    }
}
