package com.tricol.management.supplier.repository;

import com.tricol.management.supplier.domain.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
    List<Fournisseur> findAllByOrderByNomAsc();
    List<Fournisseur> findByNom(String nom);
    List<Fournisseur> findByEmailEndingWith(String emailSuffix);
}
