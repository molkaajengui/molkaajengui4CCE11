package tn.esprit.molkaajengui4cce11.domaine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEquipement;

    private String libelle;

    @ManyToMany(mappedBy = "equipements")
    @ToString.Exclude
    @Builder.Default
    @JsonIgnoreProperties("equipements")
    private List<Vehicule> vehicules = new ArrayList<>();
}
