package tn.esprit.autoloc;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AgenceTests {

    @Autowired
    private AgenceRepositoryMock basicAgenceRepository;

    @Autowired
    private IAgenceRepository fullAgenceRepository;


    private void addAgence(CrudRepository<Agence, Long> repository) {
        Agence agence = new Agence();
        agence.setAdresse("1 Rue Hedi");
        agence.setNom("Agence ariana");
        agence.setTelephone("71585874");
        agence.setVille("Tunis");

        int ms = (int) System.currentTimeMillis();

        Vehicule v1 = new Vehicule();
        v1.setCategorie(CategorieVehicule.SUV);
        v1.setImmatriculation("785414TU96-" + ms);
        v1.setMarque("Isuzu");
        v1.setModele("DMax");
        v1.setStatut(StatutVehicule.MAINTENANCE);
        v1.setTarifJournalier(new BigDecimal("100"));
        v1.setAgence(agence);

        Vehicule v2 = new Vehicule();
        v2.setCategorie(CategorieVehicule.UTILITAIRE);
        v2.setImmatriculation("785414TU95-" + ms);
        v2.setMarque("Toyota");
        v2.setModele("Yaris");
        v2.setStatut(StatutVehicule.DISPONIBLE);
        v2.setTarifJournalier(new BigDecimal("80"));
        v2.setAgence(agence);

        agence.getVehicules().add(v1);
        agence.getVehicules().add(v2);

        repository.save(agence);
    }

    @Test
    @Order(1)
    @Rollback(false)
    @Transactional
    public void basicAddAgence() {
        addAgence(basicAgenceRepository);
    }


    @Test
    @Order(2)
    @Rollback(false)
    @Transactional
    public void fullAddAgence() {
        addAgence(fullAgenceRepository);
    }



    private void loadAgence(CrudRepository<Agence, Long> repository, String repositoryType) {
        Iterable<Agence> all = repository.findAll();

        StringBuilder sb = new StringBuilder("\n");
        sb.append("=== Type de dépôt utilisé : ").append(repositoryType).append(" ===\n");

        for (Agence agence : all) {
            sb.append(agence.getIdAgence()).append(" | ").append(agence.getNom()).append("\n");

            sb.append("Vehicules Count : ").append(agence.getVehicules().size()).append("\n");

            for (Vehicule v : agence.getVehicules()) {
                sb.append("=== ").append(v.getIdVehicule()).append("|").append(v.getImmatriculation()).append("\n");
            }
        }

        fail(sb.toString());
    }

    @Test
    @Order(3)
    public void basicLoadAgence() {
        loadAgence(basicAgenceRepository, "basicAgenceRepository (CrudRepository)");
    }

    @Test
    @Order(4)
    public void fullLoadAgence() {
        loadAgence(fullAgenceRepository, "fullAgenceRepository (IAgenceRepository / JpaRepository)");
    }

    // =========================================================
    // Q11-14 : loadSortedAgences — tri par id décroissant, sans détails véhicules
    // =========================================================
    @Test
    @Order(5)
    public void loadSortedAgences() {
        // Q12 : charger toutes les agences triées par id décroissant
        List<Agence> agences = fullAgenceRepository.findAll(Sort.by(Sort.Direction.DESC, "idAgence"));

        // Q13 : afficher les informations sur les agences (sans les détails sur les véhicules)
        StringBuilder sb = new StringBuilder("\n=== loadSortedAgences (tri par id DESC) ===\n");
        for (Agence agence : agences) {
            sb.append(agence.getIdAgence())
              .append(" | ").append(agence.getNom())
              .append(" | ").append(agence.getVille())
              .append(" | ").append(agence.getAdresse())
              .append(" | ").append(agence.getTelephone())
              .append("\n");
        }

        // Q14 : vérifier le résultat
        fail(sb.toString());
    }

    // =========================================================
    // Q15-18 : loadPagedAgences — tri par id DESC + pagination par lot de 2
    // Q17 : code dupliqué intentionnellement (pas de refactoring)
    // =========================================================
    @Test
    @Order(6)
    public void loadPagedAgences() {
        Sort sort = Sort.by(Sort.Direction.DESC, "idAgence");
        int pageSize = 2;

        Page<Agence> firstPage = fullAgenceRepository.findAll(PageRequest.of(0, pageSize, sort));
        int totalPages = firstPage.getTotalPages();

        StringBuilder sb = new StringBuilder("\n=== loadPagedAgences (tri par id DESC, lot de 2) ===\n");
        sb.append("Nombre total de pages : ").append(totalPages).append("\n");

        for (int pageNumber = 0; pageNumber < totalPages; pageNumber++) {
            Page<Agence> page = fullAgenceRepository.findAll(PageRequest.of(pageNumber, pageSize, sort));

            sb.append("\n--- Page ").append(page.getNumber() + 1)
              .append(" / ").append(totalPages).append(" ---\n");


            for (Agence agence : page.getContent()) {
                sb.append(agence.getIdAgence())
                  .append(" | ").append(agence.getNom())
                  .append(" | ").append(agence.getVille())
                  .append(" | ").append(agence.getAdresse())
                  .append(" | ").append(agence.getTelephone())
                  .append("\n");
            }
        }

        fail(sb.toString());
    }
}

@Repository
interface AgenceRepositoryMock extends CrudRepository<Agence, Long> {
}
