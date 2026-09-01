package mas.s31219.repository;

import jakarta.persistence.EntityManager;
import mas.s31219.model.CzlonekKlubu;
import mas.s31219.model.Rezerwacja;
import mas.s31219.model.SesjaZajec;
import mas.s31219.model.StatusRezerwacji;

import java.util.List;

public class RezerwacjaRepository extends JpaGenericRepository<Rezerwacja> {

    public RezerwacjaRepository(EntityManager entityManager) {
        super(entityManager, Rezerwacja.class);
    }

    public List<Rezerwacja> findBySesjaZajec(SesjaZajec sesjaZajec) {
        if (sesjaZajec == null) {
            throw new IllegalArgumentException("Sesja zajęć nie może być null.");
        }

        return entityManager
                .createQuery("""
                        SELECT r
                        FROM Rezerwacja r
                        WHERE r.sesjaZajec = :sesjaZajec
                        ORDER BY r.dataRezerwacji ASC
                        """, Rezerwacja.class)
                .setParameter("sesjaZajec", sesjaZajec)
                .getResultList();
    }

    public List<Rezerwacja> findAktywneByCzlonekKlubu(CzlonekKlubu czlonekKlubu) {
        if (czlonekKlubu == null) {
            throw new IllegalArgumentException("Członek klubu nie może być null.");
        }

        return entityManager
                .createQuery("""
                        SELECT r
                        FROM Rezerwacja r
                        WHERE r.czlonekKlubu = :czlonekKlubu
                          AND r.status = :status
                        ORDER BY r.dataRezerwacji ASC
                        """, Rezerwacja.class)
                .setParameter("czlonekKlubu", czlonekKlubu)
                .setParameter("status", StatusRezerwacji.AKTYWNA)
                .getResultList();
    }

    // Sprawdza w bazie, czy istnieje już aktywna rezerwacja
// danego członka na daną sesję.
// Używane w RezerwacjaService przed utworzeniem nowej rezerwacji.
    public boolean existsAktywnaRezerwacja(CzlonekKlubu czlonekKlubu, SesjaZajec sesjaZajec) {
        if (czlonekKlubu == null) {
            throw new IllegalArgumentException("Członek klubu nie może być null.");
        }
        if (sesjaZajec == null) {
            throw new IllegalArgumentException("Sesja zajęć nie może być null.");
        }

        Long count = entityManager
                .createQuery("""
                    SELECT COUNT(r)
                    FROM Rezerwacja r
                    WHERE r.czlonekKlubu = :czlonekKlubu
                      AND r.sesjaZajec = :sesjaZajec
                      AND r.status = :status
                    """, Long.class)
                .setParameter("czlonekKlubu", czlonekKlubu)
                .setParameter("sesjaZajec", sesjaZajec)
                .setParameter("status", StatusRezerwacji.AKTYWNA)
                .getSingleResult();

        return count > 0;
    }
}