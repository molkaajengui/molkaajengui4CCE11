package tn.esprit.molkaajengui4cce11.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.molkaajengui4cce11.domaine.Contrat;

public interface IContratRepository extends JpaRepository<Contrat, Long> {
}
