package tn.esprit.molkaajengui4cce11.service;

import java.util.List;
import tn.esprit.molkaajengui4cce11.domaine.Equipement;

public interface IEquipementService {
    Equipement create(Equipement equipement);
    Equipement findById(Long id);
    List<Equipement> findAll();
    Equipement update(Long id, Equipement equipement);
    void deleteById(Long id);
}
