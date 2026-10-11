package tn.esprit.molkaajengui4cce11.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.molkaajengui4cce11.domaine.Vehicule;

public interface IVehiculeRepository extends JpaRepository<Vehicule, Long> {
}
