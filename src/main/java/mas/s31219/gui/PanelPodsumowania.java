package mas.s31219.gui;

import mas.s31219.model.CzlonekKlubu;
import mas.s31219.model.KarnetAktywny;
import mas.s31219.model.SesjaZajec;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class PanelPodsumowania extends JPanel {

    private final Runnable onWroc;
    private final Runnable onPotwierdz;
    private final Runnable onAnuluj;

    // Lewy blok podsumowania - dane wybranej sesji.
    private final JPanel daneSesjiPanel = new JPanel(new GridLayout(7, 1, 5, 5));

    // Prawy blok podsumowania - dane wybranego członka.
    private final JPanel daneCzlonkaPanel = new JPanel(new GridLayout(7, 1, 5, 5));

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Panel podsumowania nie wykonuje logiki rezerwacji.
// Dostaje akcje jako callbacki i wyświetla dane wybranej sesji oraz członka.
    public PanelPodsumowania(Runnable onWroc,
                             Runnable onPotwierdz,
                             Runnable onAnuluj) {
        if (onWroc == null) {
            throw new IllegalArgumentException("Akcja powrotu nie może być null.");
        }
        if (onPotwierdz == null) {
            throw new IllegalArgumentException("Akcja potwierdzenia nie może być null.");
        }
        if (onAnuluj == null) {
            throw new IllegalArgumentException("Akcja anulowania nie może być null.");
        }

        this.onWroc = onWroc;
        this.onPotwierdz = onPotwierdz;
        this.onAnuluj = onAnuluj;

        ustawPanel();
        zbudujWidok();
    }

    private void ustawPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(GuiStyle.KOLOR_TLA);
    }

    private void zbudujWidok() {
        JPanel panelBazowy = GuiStyle.utworzPanelBazowy("Podsumowanie rezerwacji");

        JPanel danePanel = new JPanel(new GridLayout(1, 2, 20, 0));
        danePanel.setBackground(GuiStyle.KOLOR_PANELU);
        danePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        daneSesjiPanel.setBackground(Color.WHITE);
        daneSesjiPanel.setBorder(BorderFactory.createTitledBorder("Dane sesji"));

        daneCzlonkaPanel.setBackground(Color.WHITE);
        daneCzlonkaPanel.setBorder(BorderFactory.createTitledBorder("Dane członka"));

        danePanel.add(daneSesjiPanel);
        danePanel.add(daneCzlonkaPanel);

        panelBazowy.add(danePanel, BorderLayout.CENTER);
        panelBazowy.add(utworzPanelPrzyciskow(), BorderLayout.SOUTH);

        add(panelBazowy, BorderLayout.CENTER);
    }

    private JPanel utworzPanelPrzyciskow() {
        JPanel buttons = GuiStyle.utworzPanelPrzyciskow();

        JButton wrocButton = GuiStyle.utworzPrzycisk("Wróć", GuiStyle.KOLOR_WROC);
        wrocButton.addActionListener(e -> onWroc.run());

        // Przycisk potwierdzenia rezerwacji.
// PanelPodsumowania nie tworzy sam rezerwacji,
// tylko wywołuje callback przekazany z RezerwacjaFrame.
        JButton potwierdzButton = GuiStyle.utworzPrzycisk("Potwierdź", GuiStyle.KOLOR_POTWIERDZ);
        potwierdzButton.addActionListener(e -> onPotwierdz.run());

        JButton anulujButton = GuiStyle.utworzPrzycisk("Anuluj", GuiStyle.KOLOR_ANULUJ);
        anulujButton.addActionListener(e -> onAnuluj.run());

        buttons.add(wrocButton);
        buttons.add(potwierdzButton);
        buttons.add(anulujButton);

        return buttons;
    }

    // Główna metoda panelu podsumowania.
// Dostaje wybraną sesję i członka, sprawdza je,
// wypełnia oba bloki danych i odświeża GUI.
    public void wyswietlDane(SesjaZajec sesjaZajec, CzlonekKlubu czlonekKlubu) {
        if (sesjaZajec == null) {
            throw new IllegalArgumentException("Sesja zajęć nie może być null.");
        }
        if (czlonekKlubu == null) {
            throw new IllegalArgumentException("Członek klubu nie może być null.");
        }

        // Lewa kolumna - dane sesji.
        wypelnijDaneSesji(sesjaZajec);

        // Prawa kolumna - dane członka.
        wypelnijDaneCzlonka(czlonekKlubu);

        // Odświeżenie wyglądu panelu po zmianie etykiet.
        revalidate();
        repaint();
    }

    // Wypełnia lewy blok podsumowania danymi wybranej sesji.
// Dane są pobierane z obiektu SesjaZajec wybranego na pierwszym ekranie.
    private void wypelnijDaneSesji(SesjaZajec sesjaZajec) {
        // Usuwamy poprzednie etykiety, żeby nie dublować danych przy ponownym wejściu.
        daneSesjiPanel.removeAll();

        daneSesjiPanel.add(new JLabel("Nazwa zajęć: " + sesjaZajec.getZajeciaGrupowe().getNazwa()));
        daneSesjiPanel.add(new JLabel("Termin: " + sesjaZajec.getDataCzasRozpoczecia().format(formatter)));
        daneSesjiPanel.add(new JLabel("Trener: "
                + sesjaZajec.getTrener().getImie()
                + " "
                + sesjaZajec.getTrener().getNazwisko()));
        daneSesjiPanel.add(new JLabel("Sala: " + sesjaZajec.getSalaTreningowa().getNumerSali()));
        daneSesjiPanel.add(new JLabel("Status: " + sesjaZajec.getStatus()));
        daneSesjiPanel.add(new JLabel("Limit osób: " + sesjaZajec.getLimitOsob()));

        // Dostępne miejsca są atrybutem pochodnym:
        // limit osób minus liczba aktywnych rezerwacji.
        daneSesjiPanel.add(new JLabel("Dostępne miejsca: " + sesjaZajec.getDostepneMiejsca()));
    }

    // Wypełnia prawy blok podsumowania danymi wybranego członka.
// Pokazuje też informację o aktywnym karnecie i planie członkostwa.
    private void wypelnijDaneCzlonka(CzlonekKlubu czlonekKlubu) {
        // Usuwamy poprzednie etykiety, żeby nie dublować danych.
        daneCzlonkaPanel.removeAll();

        daneCzlonkaPanel.add(new JLabel("Numer członka: " + czlonekKlubu.getNumerCzlonka()));
        daneCzlonkaPanel.add(new JLabel("Imię: " + czlonekKlubu.getImie()));
        daneCzlonkaPanel.add(new JLabel("Nazwisko: " + czlonekKlubu.getNazwisko()));
        daneCzlonkaPanel.add(new JLabel("Email: " + czlonekKlubu.getEmail()));

        // Jeśli członek ma aktywny aktualny karnet, pokazujemy jego status i plan.
        if (czlonekKlubu.czyMaAktywnyKarnet()) {
            KarnetAktywny aktywnyKarnet = czlonekKlubu.pobierzAktywnyKarnet();

            daneCzlonkaPanel.add(new JLabel("Status karnetu: " + aktywnyKarnet.getStatusKarnetu()));
            daneCzlonkaPanel.add(new JLabel("Plan: " + aktywnyKarnet.getPlanCzlonkostwa().getNazwa()));
            daneCzlonkaPanel.add(new JLabel("Zajęcia grupowe: "
                    + (aktywnyKarnet.getPlanCzlonkostwa().isPozwalaNaZajeciaGrupowe() ? "tak" : "nie")));
        } else {
            // Jeśli brak aktywnego karnetu, pokazujemy to informacyjnie.
            // Ostateczny błąd zostanie obsłużony później w RezerwacjaService po kliknięciu Potwierdź.
            daneCzlonkaPanel.add(new JLabel("Status karnetu: brak aktywnego"));
            daneCzlonkaPanel.add(new JLabel("Plan: brak"));
            daneCzlonkaPanel.add(new JLabel("Zajęcia grupowe: nie"));
        }
    }
}