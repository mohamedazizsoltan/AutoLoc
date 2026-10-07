package tn.esprit.autoloc;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RelationsTests {

    @Autowired
    private RelationAgenceRepo agenceRepo;

    @Autowired
    private RelationVehiculeRepo vehiculeRepo;

    @Autowired
    private RelationClientRepo clientRepo;

    @Autowired
    private RelationContratRepo contratRepo;

    @Autowired
    private RelationPaiementRepo paiementRepo;

    @Autowired
    private RelationMaintenanceRepo maintenanceRepo;

    @Autowired
    private RelationEquipementRepo equipementRepo;

    @Autowired
    private RelationReservationRepo reservationRepo;

    // ===== ÉTAPE 18 : Contrat + Paiement =====
    @Test
    @Order(1)
    @Rollback(false)
    @Transactional
    public void testContratPaiement() {
        Contrat contrat = new Contrat();
        contrat.setDateSignature(LocalDate.now());
        contrat.setMontantTotal(new BigDecimal("500.00"));
        contrat.setValide(true);

        Paiement p1 = new Paiement();
        p1.setMontant(new BigDecimal("250.00"));
        p1.setDatePaiement(LocalDate.now());
        p1.setModePaiement(ModePaiement.ESPECES);
        p1.setContrat(contrat);

        Paiement p2 = new Paiement();
        p2.setMontant(new BigDecimal("250.00"));
        p2.setDatePaiement(LocalDate.now().plusDays(7));
        p2.setModePaiement(ModePaiement.CARTE);
        p2.setContrat(contrat);

        contrat.getPaiements().add(p1);
        contrat.getPaiements().add(p2);

        contratRepo.save(contrat);

        StringBuilder sb = new StringBuilder("\n=== Étape 18 : Contrat + Paiements ===\n");
        for (Contrat c : contratRepo.findAll()) {
            sb.append("Contrat ").append(c.getIdContrat())
              .append(" | montant: ").append(c.getMontantTotal()).append("\n");
            sb.append("  Paiements Count : ").append(c.getPaiements().size()).append("\n");
            for (Paiement p : c.getPaiements()) {
                sb.append("  === ").append(p.getIdPaiement())
                  .append(" | ").append(p.getMontant())
                  .append(" | ").append(p.getModePaiement()).append("\n");
            }
        }
        fail(sb.toString());
    }

    // ===== ÉTAPE 19 : Maintenance + Vehicule =====
    @Test
    @Order(2)
    @Rollback(false)
    @Transactional
    public void testMaintenanceVehicule() {
        // Récupérer un véhicule existant (créé par AgenceTests)
        Vehicule vehicule = vehiculeRepo.findAll().iterator().next();

        Maintenance m = new Maintenance();
        m.setDateDebut(LocalDate.now());
        m.setDateFin(LocalDate.now().plusDays(3));
        m.setDescription("Vidange moteur");
        m.setVehicule(vehicule);

        maintenanceRepo.save(m);

        StringBuilder sb = new StringBuilder("\n=== Étape 19 : Maintenance + Vehicule ===\n");
        for (Maintenance maintenance : maintenanceRepo.findAll()) {
            sb.append("Maintenance ").append(maintenance.getIdMaintenance())
              .append(" | ").append(maintenance.getDescription())
              .append(" | véhicule: ").append(maintenance.getVehicule().getImmatriculation()).append("\n");
        }
        fail(sb.toString());
    }

    // ===== ÉTAPE 20 : Reservation + Contrat =====
    @Test
    @Order(3)
    @Rollback(false)
    @Transactional
    public void testReservationContrat() {
        Vehicule vehicule = vehiculeRepo.findAll().iterator().next();

        Client client = new Client();
        client.setNom("Ben Ali");
        client.setPrenom("Sami");
        client.setEmail("sami@email.com");
        client.setTelephone("22334455");
        client.setNumPermis("TN123456");
        client.setDateInscription(LocalDate.now());

        Contrat contrat = new Contrat();
        contrat.setDateSignature(LocalDate.now());
        contrat.setMontantTotal(new BigDecimal("300.00"));
        contrat.setValide(true);

        Reservation reservation = new Reservation();
        reservation.setDateDebut(LocalDate.now());
        reservation.setDateFin(LocalDate.now().plusDays(5));
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation.setVehicule(vehicule);
        reservation.setClient(client);
        reservation.setContrat(contrat);

        client.getReservations().add(reservation);
        clientRepo.save(client);

        StringBuilder sb = new StringBuilder("\n=== Étape 20 : Reservation + Contrat ===\n");
        for (Reservation r : reservationRepo.findAll()) {
            sb.append("Reservation ").append(r.getIdReservation())
              .append(" | statut: ").append(r.getStatut());
            if (r.getContrat() != null) {
                sb.append(" | contrat_id: ").append(r.getContrat().getIdContrat());
            }
            sb.append("\n");
        }
        fail(sb.toString());
    }

    // ===== ÉTAPE 21 : Equipement + Vehicule (ManyToMany) =====
    @Test
    @Order(4)
    @Rollback(false)
    @Transactional
    public void testEquipementVehicule() {
        Vehicule vehicule = vehiculeRepo.findAll().iterator().next();

        Equipement e1 = new Equipement();
        e1.setLibelle("GPS");

        Equipement e2 = new Equipement();
        e2.setLibelle("Climatisation");

        equipementRepo.save(e1);
        equipementRepo.save(e2);

        vehicule.getEquipements().add(e1);
        vehicule.getEquipements().add(e2);
        vehiculeRepo.save(vehicule);

        StringBuilder sb = new StringBuilder("\n=== Étape 21 : Vehicule + Equipements (table jointure) ===\n");
        for (Vehicule v : vehiculeRepo.findAll()) {
            if (!v.getEquipements().isEmpty()) {
                sb.append("Vehicule ").append(v.getIdVehicule())
                  .append(" | ").append(v.getImmatriculation()).append("\n");
                sb.append("  Equipements Count : ").append(v.getEquipements().size()).append("\n");
                for (Equipement e : v.getEquipements()) {
                    sb.append("  === ").append(e.getIdEquipement())
                      .append(" | ").append(e.getLibelle()).append("\n");
                }
            }
        }
        fail(sb.toString());
    }
}

@Repository interface RelationAgenceRepo extends CrudRepository<Agence, Long> {}
@Repository interface RelationVehiculeRepo extends CrudRepository<Vehicule, Long> {}
@Repository interface RelationClientRepo extends CrudRepository<Client, Long> {}
@Repository interface RelationContratRepo extends CrudRepository<Contrat, Long> {}
@Repository interface RelationPaiementRepo extends CrudRepository<Paiement, Long> {}
@Repository interface RelationMaintenanceRepo extends CrudRepository<Maintenance, Long> {}
@Repository interface RelationEquipementRepo extends CrudRepository<Equipement, Long> {}
@Repository interface RelationReservationRepo extends CrudRepository<Reservation, Long> {}
