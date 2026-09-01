package mas.s31219.service;

import jakarta.persistence.EntityManager;
import mas.s31219.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DaneStartoweService {

    private final EntityManager entityManager;

    public DaneStartoweService(EntityManager entityManager) {
        if (entityManager == null) {
            throw new IllegalArgumentException("EntityManager nie może być null.");
        }

        this.entityManager = entityManager;
    }

    // Serwis tworzy przykładowe dane tylko wtedy,
// gdy baza jest pusta. Dzięki temu przy każdym starcie
// nie powielamy tych samych obiektów.
    public void utworzDaneJesliBazaJestPusta() {

        // Jeżeli w bazie są już członkowie i sesje,
        // uznajemy, że dane startowe zostały wcześniej utworzone.
        if (!czyBazaJestPusta()) {
            System.out.println("Dane startowe już istnieją - pomijam tworzenie.");
            return;
        }

        // Tworzenie danych musi być w transakcji,
        // bo zapisujemy wiele powiązanych encji.
        entityManager.getTransaction().begin();

        try {
            // Tworzy cały zestaw danych testowych.
            utworzDaneStartowe();

            // Zatwierdzenie zmian w bazie.
            entityManager.getTransaction().commit();

            System.out.println("Dane startowe zostały utworzone.");
        } catch (RuntimeException e) {

            // Jeżeli wystąpi błąd, wycofujemy całą transakcję,
            // żeby baza nie została w stanie częściowo utworzonych danych.
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw e;
        }
    }

    // Sprawdza, czy baza jest pusta na podstawie liczby członków i sesji.
// Jeśli nie ma ani członków, ani sesji, tworzymy dane startowe.
    private boolean czyBazaJestPusta() {
        Long liczbaCzlonkow = entityManager
                .createQuery("""
                    SELECT COUNT(c)
                    FROM CzlonekKlubu c
                    """, Long.class)
                .getSingleResult();

        Long liczbaSesji = entityManager
                .createQuery("""
                    SELECT COUNT(s)
                    FROM SesjaZajec s
                    """, Long.class)
                .getSingleResult();

        return liczbaCzlonkow == 0 && liczbaSesji == 0;
    }

    private void utworzDaneStartowe() {
        PracownikKlubu pracownik = new PracownikKlubu(
                "Maria",
                "Wisniewska",
                LocalDate.of(1995, 8, 12),
                "600700800",
                "maria.wisniewska@example.com",
                "P001",
                LocalDate.of(2023, 6, 1),
                "Recepcjonistka",
                "ADMIN"
        );

        Trener trenerSilowy = new Trener(
                "Adam",
                "Nowak",
                LocalDate.of(1990, 3, 20),
                "501501501",
                "adam.nowak@example.com",
                "T001",
                "Trening siłowy",
                new BigDecimal("120.00")
        );
        trenerSilowy.dodajCertyfikat("Instruktor fitness");

        Trener trenerCardio = new Trener(
                "Ewa",
                "Kaczmarek",
                LocalDate.of(1992, 11, 4),
                "502502502",
                "ewa.kaczmarek@example.com",
                "T002",
                "Cardio",
                new BigDecimal("100.00")
        );
        trenerCardio.dodajCertyfikat("Instruktor zajęć grupowych");

        CzlonekKlubu czlonekPremium = new CzlonekKlubu(
                "Jan",
                "Kowalski",
                LocalDate.of(1998, 5, 10),
                "500600700",
                "jan.kowalski@example.com",
                "C001",
                LocalDate.of(2024, 1, 15),
                0,
                null
        );
        czlonekPremium.dodajKontaktAwaryjny("Anna Kowalska, 500111222");

        CzlonekKlubu czlonekBezKarnetu = new CzlonekKlubu(
                "Piotr",
                "Zielinski",
                LocalDate.of(1997, 4, 3),
                "511222333",
                "piotr.zielinski@example.com",
                "C002",
                LocalDate.of(2024, 2, 10),
                0,
                null
        );

        CzlonekKlubu czlonekBasic = new CzlonekKlubu(
                "Anna",
                "Nowicka",
                LocalDate.of(1999, 9, 21),
                "522333444",
                "anna.nowicka@example.com",
                "C003",
                LocalDate.of(2024, 3, 5),
                0,
                null
        );

        CzlonekKlubu czlonekPremium2 = new CzlonekKlubu(
                "Tomasz",
                "Lewandowski",
                LocalDate.of(1996, 7, 11),
                "533444555",
                "tomasz.lewandowski@example.com",
                "C004",
                LocalDate.of(2024, 4, 1),
                0,
                null
        );

        entityManager.persist(pracownik);
        entityManager.persist(trenerSilowy);
        entityManager.persist(trenerCardio);
        entityManager.persist(czlonekPremium);
        entityManager.persist(czlonekBezKarnetu);
        entityManager.persist(czlonekBasic);
        entityManager.persist(czlonekPremium2);

        PlanCzlonkostwa planPremium = new PlanCzlonkostwa(
                "Premium",
                30,
                new BigDecimal("149.99"),
                true
        );

        PlanCzlonkostwa planBasic = new PlanCzlonkostwa(
                "Basic",
                30,
                new BigDecimal("79.99"),
                false
        );

        entityManager.persist(planPremium);
        entityManager.persist(planBasic);

        KarnetAktywny karnetPremium = utworzKarnetAktywny(planPremium, czlonekPremium);
        KarnetAktywny karnetBasic = utworzKarnetAktywny(planBasic, czlonekBasic);
        KarnetAktywny karnetPremium2 = utworzKarnetAktywny(planPremium, czlonekPremium2);

        entityManager.persist(karnetPremium);
        entityManager.persist(karnetBasic);
        entityManager.persist(karnetPremium2);

        OddzialKlubu oddzial = new OddzialKlubu(
                "Klub Fitness Centrum",
                "ul. Sportowa 10, Warszawa",
                "500600700",
                "06:00-23:00"
        );

        SalaTreningowa salaSilowa = oddzial.dodajSale(
                "S01",
                "Sala siłowa",
                20,
                80.5
        );

        SalaTreningowa salaCardio = oddzial.dodajSale(
                "S02",
                "Sala cardio",
                15,
                55.0
        );

        SalaTreningowa malaSala = oddzial.dodajSale(
                "S03",
                "Mała sala treningowa",
                1,
                25.0
        );

        entityManager.persist(oddzial);

        ZajeciaGrupowe treningSilowy = new ZajeciaGrupowe(
                "Trening siłowy",
                "Zajęcia wzmacniające całe ciało.",
                "średni",
                "siłowe",
                60,
                15
        );

        ZajeciaGrupowe cardio = new ZajeciaGrupowe(
                "Cardio",
                "Zajęcia poprawiające wydolność.",
                "łatwy",
                "cardio",
                45,
                12
        );

        entityManager.persist(treningSilowy);
        entityManager.persist(cardio);

        SesjaZajec sesjaSilowa = new SesjaZajec(
                LocalDateTime.now().plusDays(1).withHour(18).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(1).withHour(19).withMinute(0).withSecond(0).withNano(0),
                15,
                StatusSesji.ZAPLANOWANA,
                treningSilowy,
                salaSilowa,
                trenerSilowy,
                pracownik
        );

        SesjaZajec sesjaCardio = new SesjaZajec(
                LocalDateTime.now().plusDays(2).withHour(17).withMinute(30).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(2).withHour(18).withMinute(15).withSecond(0).withNano(0),
                12,
                StatusSesji.ZAPLANOWANA,
                cardio,
                salaCardio,
                trenerCardio,
                pracownik
        );

        SesjaZajec sesjaMala = new SesjaZajec(
                LocalDateTime.now().plusDays(3).withHour(16).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(3).withHour(17).withMinute(0).withSecond(0).withNano(0),
                1,
                StatusSesji.ZAPLANOWANA,
                treningSilowy,
                malaSala,
                trenerSilowy,
                pracownik
        );

        SesjaZajec sesjaAnulowana = new SesjaZajec(
                LocalDateTime.now().plusDays(4).withHour(19).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(4).withHour(20).withMinute(0).withSecond(0).withNano(0),
                10,
                StatusSesji.ZAPLANOWANA,
                treningSilowy,
                salaSilowa,
                trenerSilowy,
                pracownik
        );
        sesjaAnulowana.anulujSesje();

        entityManager.persist(sesjaSilowa);
        entityManager.persist(sesjaCardio);
        entityManager.persist(sesjaMala);
        entityManager.persist(sesjaAnulowana);

        Platnosc platnoscPremium = new Platnosc(
                LocalDateTime.now(),
                new BigDecimal("149.99"),
                MetodaPlatnosci.KARTA,
                StatusPlatnosci.ZAREJESTROWANA,
                "TX-001",
                karnetPremium,
                pracownik
        );

        Platnosc platnoscBasic = new Platnosc(
                LocalDateTime.now(),
                new BigDecimal("79.99"),
                MetodaPlatnosci.PRZELEW,
                StatusPlatnosci.ZAREJESTROWANA,
                "TX-002",
                karnetBasic,
                pracownik
        );

        entityManager.persist(platnoscPremium);
        entityManager.persist(platnoscBasic);
    }

    private KarnetAktywny utworzKarnetAktywny(PlanCzlonkostwa planCzlonkostwa,
                                              CzlonekKlubu czlonekKlubu) {
        LocalDate dataRozpoczecia = LocalDate.now();

        return new KarnetAktywny(
                dataRozpoczecia,
                planCzlonkostwa.obliczDateZakonczenia(dataRozpoczecia),
                BigDecimal.ZERO,
                planCzlonkostwa,
                czlonekKlubu,
                dataRozpoczecia,
                dataRozpoczecia.plusMonths(1),
                dataRozpoczecia
        );
    }
}