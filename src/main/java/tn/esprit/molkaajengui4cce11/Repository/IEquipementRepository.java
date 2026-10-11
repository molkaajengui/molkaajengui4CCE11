package tn.esprit.molkaajengui4cce11.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.molkaajengui4cce11.domaine.Equipement;

public interface IEquipementRepository extends JpaRepository<Equipement, Long> {
}
