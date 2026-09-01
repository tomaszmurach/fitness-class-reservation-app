package mas.s31219.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

// Klasa pomocnicza centralizująca konfigurację JPA.
// Dzięki temu EntityManagerFactory nie jest tworzony w wielu miejscach projektu.
public class JpaUtil {

    // EntityManagerFactory jest kosztowny w tworzeniu,
    // dlatego powstaje raz jako statyczne pole.
    // Nazwa persistence-unit musi zgadzać się z nazwą w persistence.xml.
    private static final EntityManagerFactory entityManagerFactory =
            Persistence.createEntityManagerFactory("mas-implementacja-persistence-unit");

    // Tworzy EntityManager używany do pracy z bazą danych.
    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    // Zamknięcie fabryki EntityManagerów.
    // W większej aplikacji warto wywołać przy końcu działania programu.
    public static void close() {
        entityManagerFactory.close();
    }
}
