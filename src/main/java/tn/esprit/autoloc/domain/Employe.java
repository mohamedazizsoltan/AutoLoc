package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmploye;

    private String nom;
    private String prenom;

    @Enumerated(EnumType.STRING)
    private RoleEmploye role;

    // Étape 16 : ManyToOne vers Agence (LAZY, pas de cascade)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id_agence")
    private Agence agence;
}
