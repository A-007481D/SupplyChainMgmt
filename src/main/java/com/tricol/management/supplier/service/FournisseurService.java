package com.tricol.management.supplier.service;

import com.tricol.management.supplier.domain.model.Fournisseur;
import java.util.List;
import java.util.Optional;

public interface FournisseurService {
    List<Fournisseur> findAllSortedByName();
    Optional<Fournisseur> findById(Long id);
    Fournisseur save(Fournisseur fournisseur);
    Fournisseur update(Long id, Fournisseur fournisseurDetails);
    void deleteById(Long id);

}
