package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agence")
@Getter
@Setter
@NoArgsConstructor
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgence;

    private String nom;
    private String ville;
    private String adresse;
    private String telephone;

    // Déjà existant : OneToMany vers Vehicule (EAGER + CASCADE ALL)
    @OneToMany(mappedBy = "agence", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Vehicule> vehicules = new ArrayList<>();

    // Étape 16 : OneToMany vers Employe (LAZY, pas de cascade delete)
    @OneToMany(mappedBy = "agence", fetch = FetchType.LAZY)
    private List<Employe> employes = new ArrayList<>();
}
