package mas.s31219.repository;

import jakarta.persistence.EntityManager;
import mas.s31219.model.CzlonekKlubu;

import java.util.List;
import java.util.Optional;

public class CzlonekKlubuRepository extends JpaGenericRepository<CzlonekKlubu> {

    public CzlonekKlubuRepository(EntityManager entityManager) {
        super(entityManager, CzlonekKlubu.class);
    }

    public Optional<CzlonekKlubu> findByNumerCzlonka(String numerCzlonka) {
        if (numerCzlonka == null || numerCzlonka.isBlank()) {
            throw new IllegalArgumentException("Numer członka nie może być pusty.");
        }

        List<CzlonekKlubu> result = entityManager
                .createQuery("""
                        SELECT c
                        FROM CzlonekKlubu c
                        WHERE c.numerCzlonka = :numerCzlonka
                        """, CzlonekKlubu.class)
                .setParameter("numerCzlonka", numerCzlonka)
                .getResultList();

        return result.stream().findFirst();
    }
}