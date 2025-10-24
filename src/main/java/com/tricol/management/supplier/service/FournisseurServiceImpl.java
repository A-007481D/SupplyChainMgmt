package com.tricol.management.supplier.service;

import com.tricol.management.supplier.domain.model.Fournisseur;
import com.tricol.management.supplier.repository.FournisseurRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public class FournisseurServiceImpl implements FournisseurService {

    private FournisseurRepository fournisseurRepository;

    public void setFournisseurRepository(FournisseurRepository fournisseurRepository) {
        this.fournisseurRepository = fournisseurRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fournisseur> findAllSortedByName() {
        return fournisseurRepository.findAllByOrderByNomAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Fournisseur> findById(Long id) {
        return fournisseurRepository.findById(id);
    }

    @Override
    @Transactional
    public Fournisseur save(Fournisseur fournisseur) {
        fournisseur.setId(null);
        return fournisseurRepository.save(fournisseur);
    }

    @Override
    @Transactional
    public Fournisseur update(Long id, Fournisseur fournisseurDetails) {
        Optional<Fournisseur> optionalFournisseur = fournisseurRepository.findById(id);

        if (optionalFournisseur.isPresent()) {
            Fournisseur fExistant = optionalFournisseur.get();

            fExistant.setNom(fournisseurDetails.getNom());
            fExistant.setSociete(fournisseurDetails.getSociete());
            fExistant.setAdresse(fournisseurDetails.getAdresse());
            fExistant.setContact(fournisseurDetails.getContact());
            fExistant.setEmail(fournisseurDetails.getEmail());
            fExistant.setTelephone(fournisseurDetails.getTelephone());
            fExistant.setVille(fournisseurDetails.getVille());
            fExistant.setIce(fournisseurDetails.getIce());

            return fournisseurRepository.save(fExistant);
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        fournisseurRepository.deleteById(id);
    }
}
