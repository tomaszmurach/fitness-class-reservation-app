package mas.s31219.service;

import jakarta.persistence.EntityManager;
import mas.s31219.model.CzlonekKlubu;
import mas.s31219.model.KarnetAktywny;
import mas.s31219.model.Rezerwacja;
import mas.s31219.model.SesjaZajec;
import mas.s31219.model.StatusSesji;
import mas.s31219.repository.RezerwacjaRepository;

public class RezerwacjaService {

    private final EntityManager entityManager;
    private final RezerwacjaRepository rezerwacjaRepository;

    public RezerwacjaService(EntityManager entityManager) {
        if (entityManager == null) {
            throw new IllegalArgumentException("EntityManager nie może być null.");
        }

        this.entityManager = entityManager;
        this.rezerwacjaRepository = new RezerwacjaRepository(entityManager);
    }

    // Główna metoda przypadku użycia: złożenie rezerwacji miejsca na sesję.
// Tutaj zaczyna się właściwa logika biznesowa i zapis do bazy.
    public Rezerwacja zlozRezerwacje(CzlonekKlubu czlonekKlubu, SesjaZajec sesjaZajec) {
        // Rozpoczynamy transakcję, jeśli żadna nie jest aktywna.
        rozpocznijTransakcjeJesliPotrzeba();

        try {
            // Sprawdzenie wszystkich warunków biznesowych przed utworzeniem rezerwacji.
            weryfikujMozliwoscRezerwacji(czlonekKlubu, sesjaZajec);

            // Utworzenie rezerwacji przez metodę domenową SesjaZajec.
            // Rezerwacja nie jest tworzona bezpośrednio w GUI.
            Rezerwacja rezerwacja = sesjaZajec.zarezerwujMiejsce(czlonekKlubu);

            // Zapis nowej rezerwacji do bazy przez repozytorium.
            rezerwacjaRepository.save(rezerwacja);

            // Zatwierdzenie zmian w bazie.
            zatwierdzTransakcjeJesliAktywna();

            // Zwracamy utworzoną rezerwację do GUI.
            return rezerwacja;
        } catch (RuntimeException e) {
            // W przypadku błędu wycofujemy całą transakcję,
            // żeby nie zostawić częściowo zapisanych danych.
            wycofajTransakcjeJesliAktywna();
            throw e;
        }
    }

    // Sprawdza wszystkie warunki biznesowe wymagane do utworzenia rezerwacji.
// Jeżeli którykolwiek warunek nie jest spełniony, metoda rzuca wyjątek,
// a transakcja w zlozRezerwacje() zostanie wycofana.
    public void weryfikujMozliwoscRezerwacji(CzlonekKlubu czlonekKlubu, SesjaZajec sesjaZajec) {
        if (czlonekKlubu == null) {
            throw new IllegalArgumentException("Do utworzenia rezerwacji wymagane jest wskazanie członka klubu.");
        }

        if (sesjaZajec == null) {
            throw new IllegalArgumentException("Do utworzenia rezerwacji wymagane jest wskazanie sesji zajęć.");
        }

        // Rezerwacja wymaga aktywnego aktualnego karnetu.
        if (!czlonekKlubu.czyMaAktywnyKarnet()) {
            throw new IllegalStateException("Rezerwacja miejsca na sesję zajęć wymaga aktywnego karnetu.");
        }

        // Pobieramy aktywny karnet, żeby sprawdzić plan członkostwa.
        KarnetAktywny aktywnyKarnet = czlonekKlubu.pobierzAktywnyKarnet();

        // Plan członkostwa musi pozwalać na udział w zajęciach grupowych.
        if (!aktywnyKarnet.getPlanCzlonkostwa().isPozwalaNaZajeciaGrupowe()) {
            throw new IllegalStateException("Aktualny plan członkostwa nie pozwala na udział w zajęciach grupowych.");
        }

        // Rezerwować można tylko sesje zaplanowane.
        if (sesjaZajec.getStatus() != StatusSesji.ZAPLANOWANA) {
            throw new IllegalStateException("Rezerwacja jest możliwa wyłącznie dla zaplanowanej sesji zajęć.");
        }

        // Sesja musi mieć wolne miejsca.
        if (!sesjaZajec.czyMaWolneMiejsca()) {
            throw new IllegalStateException("Brak dostępnych miejsc na wybranej sesji zajęć.");
        }

        // Nie można mieć dwóch aktywnych rezerwacji na tę samą sesję.
        // To sprawdzenie jest wykonywane przez repozytorium na danych z bazy.
        if (rezerwacjaRepository.existsAktywnaRezerwacja(czlonekKlubu, sesjaZajec)) {
            throw new IllegalStateException("Członek posiada już aktywną rezerwację na wybraną sesję zajęć.");
        }
    }

    public void anulujRezerwacje(Rezerwacja rezerwacja) {
        if (rezerwacja == null) {
            throw new IllegalArgumentException("Rezerwacja jest wymagana.");
        }

        rozpocznijTransakcjeJesliPotrzeba();

        try {
            rezerwacja.anuluj();
            rezerwacjaRepository.update(rezerwacja);

            zatwierdzTransakcjeJesliAktywna();
        } catch (RuntimeException e) {
            wycofajTransakcjeJesliAktywna();
            throw e;
        }
    }

    public void oznaczObecnosc(SesjaZajec sesjaZajec, CzlonekKlubu czlonekKlubu) {
        if (sesjaZajec == null) {
            throw new IllegalArgumentException("Sesja zajęć jest wymagana.");
        }
        if (czlonekKlubu == null) {
            throw new IllegalArgumentException("Członek klubu jest wymagany.");
        }

        rozpocznijTransakcjeJesliPotrzeba();

        try {
            sesjaZajec.oznaczObecnosc(czlonekKlubu);

            zatwierdzTransakcjeJesliAktywna();
        } catch (RuntimeException e) {
            wycofajTransakcjeJesliAktywna();
            throw e;
        }
    }

    public void oznaczNieobecnosc(SesjaZajec sesjaZajec, CzlonekKlubu czlonekKlubu) {
        if (sesjaZajec == null) {
            throw new IllegalArgumentException("Sesja zajęć jest wymagana.");
        }
        if (czlonekKlubu == null) {
            throw new IllegalArgumentException("Członek klubu jest wymagany.");
        }

        rozpocznijTransakcjeJesliPotrzeba();

        try {
            sesjaZajec.oznaczNieobecnosc(czlonekKlubu);

            zatwierdzTransakcjeJesliAktywna();
        } catch (RuntimeException e) {
            wycofajTransakcjeJesliAktywna();
            throw e;
        }
    }

    // Rozpoczyna transakcję JPA, jeśli nie ma już aktywnej transakcji.
// Transakcja obejmuje walidację, utworzenie rezerwacji i zapis do bazy.
    private void rozpocznijTransakcjeJesliPotrzeba() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
    }

    // Zatwierdza transakcję, czyli utrwala zmiany w bazie.
    private void zatwierdzTransakcjeJesliAktywna() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
        }
    }

    // Wycofuje transakcję w razie błędu.
// Dzięki temu nie zostaje częściowo utworzona rezerwacja.
    private void wycofajTransakcjeJesliAktywna() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }
    }
}