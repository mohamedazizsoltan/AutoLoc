package tn.esprit.autoloc;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AgenceTests {

    @Autowired
    private AgenceRepositoryMock agenceRepository;

    @Test
    @Order(1)
    @Rollback(false)
    @Transactional
    public void addAgence() {
        Agence agence = new Agence();
        agence.setAdresse("1 Rue Hedi");
        agence.setNom("Agence ariana");
        agence.setTelephone("71585874");
        agence.setVille("Tunis");

        Vehicule v1 = new Vehicule();
        v1.setCategorie(CategorieVehicule.SUV);
        v1.setImmatriculation("785414TU96");
        v1.setMarque("Isuzu");
        v1.setModele("DMax");
        v1.setStatut(StatutVehicule.MAINTENANCE);
        v1.setTarifJournalier(new BigDecimal("100"));
        v1.setAgence(agence);

        Vehicule v2 = new Vehicule();
        v2.setCategorie(CategorieVehicule.UTILITAIRE);
        v2.setImmatriculation("785414TU95");
        v2.setMarque("Toyota");
        v2.setModele("Yaris");
        v2.setStatut(StatutVehicule.DISPONIBLE);
        v2.setTarifJournalier(new BigDecimal("80"));
        v2.setAgence(agence);

        agence.getVehicules().add(v1);
        agence.getVehicules().add(v2);

        agenceRepository.save(agence);
    }

    @Test
    @Order(2)
    public void loadAgence() {
        // 12. Récupérer la liste de toutes les agences via findAll
        Iterable<Agence> all = agenceRepository.findAll();

        // 13. Parcourir le résultat avec StringBuilder
        StringBuilder sb = new StringBuilder("\n");
        for (Agence agence : all) {
            // Identifiant et nom de l'agence
            sb.append(agence.getIdAgence()).append(" | ").append(agence.getNom()).append("\n");

            // Nombre de véhicules attachés à chaque agence
            sb.append("Vehicules Count : ").append(agence.getVehicules().size()).append("\n");

            // Identifiant et immatriculation de chaque véhicule
            for (Vehicule v : agence.getVehicules()) {
                sb.append("=== ").append(v.getIdVehicule()).append("|").append(v.getImmatriculation()).append("\n");
            }
        }

        // 14. Assertion qui échoue toujours pour afficher le résultat
        fail(sb.toString());
    }
}

@Repository
interface AgenceRepositoryMock extends CrudRepository<Agence, Long> {
}
