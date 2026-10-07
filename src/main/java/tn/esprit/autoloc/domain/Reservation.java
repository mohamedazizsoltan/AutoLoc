package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut;

    // Étape 16 : ManyToOne vers Vehicule (LAZY, cascade delete via Vehicule)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id_vehicule")
    private Vehicule vehicule;

    // Étape 16 : ManyToOne vers Client (EAGER, cascade delete via Client)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id_client")
    private Client client;

    // Étape 20 : OneToOne vers Contrat — clé FK dans la table reservation
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "contrat_id_contrat")
    private Contrat contrat;
}
