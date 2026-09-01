package mas.s31219.gui;

import jakarta.persistence.EntityManager;
import mas.s31219.model.CzlonekKlubu;
import mas.s31219.model.Rezerwacja;
import mas.s31219.model.SesjaZajec;
import mas.s31219.repository.CzlonekKlubuRepository;
import mas.s31219.repository.SesjaZajecRepository;
import mas.s31219.service.RezerwacjaService;

import javax.swing.*;
import java.awt.*;

public class RezerwacjaFrame extends JFrame {

    private static final String PANEL_SESJE = "PANEL_SESJE";
    private static final String PANEL_CZLONKOWIE = "PANEL_CZLONKOWIE";
    private static final String PANEL_PODSUMOWANIE = "PANEL_PODSUMOWANIE";
    private static final String PANEL_SUKCES = "PANEL_SUKCES";
    private static final String PANEL_BLAD = "PANEL_BLAD";

    private final EntityManager entityManager;

    private final CzlonekKlubuRepository czlonekKlubuRepository;
    // Repozytorium sesji zajęć.
// Jest używane na pierwszym ekranie GUI do pobierania sesji z bazy.
    private final SesjaZajecRepository sesjaZajecRepository;
    private final RezerwacjaService rezerwacjaService;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    private PanelWyboruSesji panelWyboruSesji;
    private PanelWyboruCzlonka panelWyboruCzlonka;
    private PanelPodsumowania panelPodsumowania;
    private PanelSukcesu panelSukcesu;
    private PanelBledu panelBledu;

    // Przechowuje sesję wybraną w pierwszym kroku procesu.
// Będzie później użyta w podsumowaniu i przy tworzeniu rezerwacji.
    private SesjaZajec wybranaSesja;
    private CzlonekKlubu wybranyCzlonek;
    private Rezerwacja ostatniaRezerwacja;

    // Główne okno aplikacji.
// Pełni rolę kontrolera przepływu GUI.
    public RezerwacjaFrame(EntityManager entityManager) {

        // Bez EntityManagera nie da się pobierać ani zapisywać danych.
        if (entityManager == null) {
            throw new IllegalArgumentException("EntityManager nie może być null.");
        }

        // Ten sam EntityManager jest przekazywany do repozytoriów i serwisu,
        // dzięki czemu GUI, serwis i repozytoria pracują na tym samym kontekście JPA.
        this.entityManager = entityManager;

        // Repozytorium członków używane później na ekranie wyboru członka.
        this.czlonekKlubuRepository = new CzlonekKlubuRepository(entityManager);

        // Repozytorium sesji używane na pierwszym ekranie wyboru sesji.
        this.sesjaZajecRepository = new SesjaZajecRepository(entityManager);

        // Serwis odpowiedzialny za właściwą logikę rezerwacji.
        this.rezerwacjaService = new RezerwacjaService(entityManager);

        // Konfiguracja parametrów okna.
        ustawOkno();

        // Utworzenie wszystkich ekranów/paneli procesu.
        utworzPanele();

        // Dodanie paneli do CardLayout, czyli mechanizmu przełączania ekranów.
        dodajPaneleDoCardLayout();

        // Dodanie głównego panelu do JFrame.
        add(mainPanel);

        // Pokazanie pierwszego ekranu z listą sesji.
        pokazPanelWyboruSesji();
    }

    private void ustawOkno() {
        setTitle("Klub Fitness - Rezerwacja miejsca na sesję zajęć");
        setSize(1250, 650);
        setMinimumSize(new Dimension(1050, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void utworzPanele() {
        // Tworzymy pierwszy panel procesu.
// Drugi argument to callback wyboru sesji.
// Gdy użytkownik kliknie "Wybierz sesję" w panelu,
// zostanie wywołana metoda RezerwacjaFrame.wybierzSesje(...).
        panelWyboruSesji = new PanelWyboruSesji(
                sesjaZajecRepository,
                this::wybierzSesje,
                this::zamknijOkno
        );

        panelWyboruCzlonka = new PanelWyboruCzlonka(
                czlonekKlubuRepository,
                this::wrocDoWyboruSesji,
                this::wybierzCzlonka,
                this::anulujProces
        );

        // Tworzymy panel podsumowania.
// Przekazujemy callbacki dla przycisków:
// Wróć -> powrót do wyboru członka,
// Potwierdź -> finalne utworzenie rezerwacji,
// Anuluj -> przerwanie procesu i powrót do pierwszego ekranu.
        panelPodsumowania = new PanelPodsumowania(
                this::wrocDoWyboruCzlonka,
                this::potwierdzRezerwacje,
                this::anulujProces
        );

        panelSukcesu = new PanelSukcesu(this::zakonczPoSukcesie);

        panelBledu = new PanelBledu(
                this::wrocDoPodsumowania,
                this::anulujProces
        );
    }

    private void dodajPaneleDoCardLayout() {
        mainPanel.add(panelWyboruSesji, PANEL_SESJE);
        mainPanel.add(panelWyboruCzlonka, PANEL_CZLONKOWIE);
        mainPanel.add(panelPodsumowania, PANEL_PODSUMOWANIE);
        mainPanel.add(panelSukcesu, PANEL_SUKCES);
        mainPanel.add(panelBledu, PANEL_BLAD);
    }

    // Pokazuje pierwszy ekran aplikacji.
// Przed pokazaniem panelu odświeżamy listę sesji,
// żeby użytkownik widział aktualne dane z bazy.
    private void pokazPanelWyboruSesji() {
        panelWyboruSesji.odswiezSesje();
        cardLayout.show(mainPanel, PANEL_SESJE);
    }

    // Obsługuje wybór sesji przekazany z PanelWyboruSesji.
// Jest wywoływana po kliknięciu przycisku "Wybierz sesję".
    private void wybierzSesje(SesjaZajec sesjaZajec) {

        // Jeśli użytkownik kliknął przycisk bez zaznaczenia sesji,
        // pokazujemy ostrzeżenie i zostajemy na pierwszym ekranie.
        if (sesjaZajec == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Najpierw wybierz sesję zajęć.",
                    "Brak wyboru",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Zapamiętujemy wybraną sesję w stanie głównego okna.
        // Ten obiekt będzie później użyty w podsumowaniu i przy rezerwacji.
        this.wybranaSesja = sesjaZajec;

        // Przed przejściem do drugiego ekranu odświeżamy listę członków.
        panelWyboruCzlonka.odswiezCzlonkow();

        // Przełączamy widok na ekran wyboru członka przez CardLayout.
        cardLayout.show(mainPanel, PANEL_CZLONKOWIE);
    }

    private void wybierzCzlonka(CzlonekKlubu czlonekKlubu) {
        if (czlonekKlubu == null) {
            pokazBlad("Do utworzenia rezerwacji wymagane jest wskazanie członka klubu.");
            return;
        }

        this.wybranyCzlonek = czlonekKlubu;

        panelPodsumowania.wyswietlDane(wybranaSesja, wybranyCzlonek);
        cardLayout.show(mainPanel, PANEL_PODSUMOWANIE);
    }

    // Obsługuje kliknięcie "Potwierdź" z panelu podsumowania.
// To jest przejście z GUI do logiki biznesowej.
// Frame nie tworzy rezerwacji samodzielnie - deleguje to do RezerwacjaService.
    private void potwierdzRezerwacje() {
        try {
            // Wywołanie serwisu z obiektami wybranymi w poprzednich krokach procesu.
            ostatniaRezerwacja = rezerwacjaService.zlozRezerwacje(wybranyCzlonek, wybranaSesja);

            // Jeśli rezerwacja została poprawnie utworzona i zapisana,
            // pokazujemy ekran sukcesu.
            panelSukcesu.wyswietlDane(ostatniaRezerwacja);
            cardLayout.show(mainPanel, PANEL_SUKCES);
        } catch (RuntimeException e) {
            // Jeżeli serwis rzuci wyjątek walidacyjny lub transakcyjny,
            // pokazujemy komunikat błędu użytkownikowi.
            pokazBlad(e.getMessage());
        }
    }

    private void pokazBlad(String komunikat) {
        panelBledu.wyswietlKomunikat(komunikat);
        cardLayout.show(mainPanel, PANEL_BLAD);
    }

    private void wrocDoWyboruSesji() {
        cardLayout.show(mainPanel, PANEL_SESJE);
    }

    private void wrocDoWyboruCzlonka() {
        panelWyboruCzlonka.odswiezCzlonkow();
        cardLayout.show(mainPanel, PANEL_CZLONKOWIE);
    }

    private void wrocDoPodsumowania() {
        cardLayout.show(mainPanel, PANEL_PODSUMOWANIE);
    }

    private void zakonczPoSukcesie() {
        resetujStanProcesu();
        panelWyboruSesji.odswiezSesje();
        cardLayout.show(mainPanel, PANEL_SESJE);
    }

    private void anulujProces() {
        resetujStanProcesu();
        panelWyboruSesji.odswiezSesje();
        cardLayout.show(mainPanel, PANEL_SESJE);
    }

    private void resetujStanProcesu() {
        wybranaSesja = null;
        wybranyCzlonek = null;
        ostatniaRezerwacja = null;

        panelWyboruSesji.wyczyscWybor();
        panelWyboruCzlonka.wyczyscWybor();
    }

    private void zamknijOkno() {
        dispose();
    }
}