package mas.s31219;

import jakarta.persistence.EntityManager;
import mas.s31219.gui.RezerwacjaFrame;
import mas.s31219.model.Rezerwacja;
import mas.s31219.service.DaneStartoweService;
import mas.s31219.util.JpaUtil;

import javax.swing.*;

public class Main {

    // Punkt startowy aplikacji.
// Tutaj zaczyna się cały przepływ programu.
    public static void main(String[] args) {

        // Pobranie EntityManagera, czyli obiektu JPA używanego do pracy z bazą danych.
        // W tym momencie Hibernate korzysta z konfiguracji persistence.xml.
        EntityManager entityManager = JpaUtil.getEntityManager();

        // Utworzenie serwisu odpowiedzialnego za dane startowe.
        // Dane są potrzebne, żeby GUI po starcie miało co wyświetlić.
        DaneStartoweService daneStartoweService = new DaneStartoweService(entityManager);

        // Jeżeli baza jest pusta, tworzone są przykładowe obiekty:
        // członkowie, trenerzy, plany, karnety, sale, sesje itd.
        daneStartoweService.utworzDaneJesliBazaJestPusta();

        // Uruchomienie GUI w wątku Swinga.
        // SwingUtilities.invokeLater zapewnia, że interfejs graficzny działa na EDT.
        SwingUtilities.invokeLater(() -> {
            RezerwacjaFrame frame = new RezerwacjaFrame(entityManager);
            frame.setVisible(true);
        });
    }
}