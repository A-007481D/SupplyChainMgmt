package com.tricol.management.supplier.repository;

import com.tricol.management.supplier.domain.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
    List<Fournisseur> findBySocieteContainingIgnoreCase(String societe);
    List<Fournisseur> findByEmailEndingWith(String suffix);
    List<Fournisseur> findAllByOrderBySocieteAsc();
}