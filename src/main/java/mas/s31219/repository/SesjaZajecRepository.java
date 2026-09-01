package mas.s31219.repository;

import jakarta.persistence.EntityManager;
import mas.s31219.model.SesjaZajec;
import mas.s31219.model.StatusSesji;

import java.util.List;

public class SesjaZajecRepository extends JpaGenericRepository<SesjaZajec> {

    public SesjaZajecRepository(EntityManager entityManager) {
        super(entityManager, SesjaZajec.class);
    }

    public List<SesjaZajec> findZaplanowane() {
        return entityManager
                .createQuery("""
                        SELECT s
                        FROM SesjaZajec s
                        WHERE s.status = :status
                        ORDER BY s.dataCzasRozpoczecia ASC
                        """, SesjaZajec.class)
                .setParameter("status", StatusSesji.ZAPLANOWANA)
                .getResultList();
    }

    // Pobiera wszystkie sesje zajęć z bazy,
// posortowane rosnąco po dacie rozpoczęcia.
// Metoda jest używana przez PanelWyboruSesji do zasilenia lewej listy.
    public List<SesjaZajec> findAllOrderByDataRozpoczecia() {
        return entityManager
                .createQuery("""
                    SELECT s
                    FROM SesjaZajec s
                    ORDER BY s.dataCzasRozpoczecia ASC
                    """, SesjaZajec.class)
                .getResultList();
    }
}