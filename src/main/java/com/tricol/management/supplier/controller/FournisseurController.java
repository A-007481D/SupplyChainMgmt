package com.tricol.management.supplier.controller;

import com.tricol.management.supplier.domain.model.Fournisseur;
import com.tricol.management.supplier.service.FournisseurService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/v1/fournisseurs")
public class FournisseurController {

    private FournisseurService fournisseurService;

    public void setFournisseurService(FournisseurService fournisseurService) {
        this.fournisseurService = fournisseurService;
    }

    @GetMapping
    @ResponseBody
    public List<Fournisseur> getAllFournisseurs() {
        return fournisseurService.findAllSortedByName();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Fournisseur> getFournisseurById(@PathVariable("id") Long id) {
        Optional<Fournisseur> fournisseur = fournisseurService.findById(id);
        return fournisseur.map(f -> ResponseEntity.ok(f))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Fournisseur> createFournisseur(@RequestBody Fournisseur fournisseur) {
        Fournisseur nouveauFournisseur = fournisseurService.save(fournisseur);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauFournisseur);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Fournisseur> updateFournisseur(@PathVariable("id") Long id, @RequestBody Fournisseur fournisseurDetails) {
        Fournisseur fModifie = fournisseurService.update(id, fournisseurDetails);

        if (fModifie != null) {
            return ResponseEntity.ok(fModifie);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteFournisseur(@PathVariable("id") Long id) {
        try {
            fournisseurService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/search/byNom")
    @ResponseBody
    public List<Fournisseur> searchByNom(@RequestParam("nom") String nom) {
        return fournisseurService.findByNom(nom);
    }

    @GetMapping("/search/byEmailSuffix")
    @ResponseBody
    public List<Fournisseur> searchByEmailSuffix(@RequestParam("suffix") String suffix) {
        return fournisseurService.findByEmailEndingWith(suffix);
    }

}

