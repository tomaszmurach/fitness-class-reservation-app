package mas.s31219.gui;

import mas.s31219.model.CzlonekKlubu;
import mas.s31219.repository.CzlonekKlubuRepository;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class PanelWyboruCzlonka extends JPanel {

    private final CzlonekKlubuRepository czlonekKlubuRepository;
    private final Runnable onWroc;
    private final Consumer<CzlonekKlubu> onWybierzCzlonka;
    private final Runnable onAnuluj;

    private final DefaultListModel<CzlonekKlubu> czlonkowieModel = new DefaultListModel<>();
    private final JList<CzlonekKlubu> czlonkowieList = new JList<>(czlonkowieModel);

    private JTextField filtrNumerCzlonkaField;
    private JTextField filtrNazwiskoCzlonkaField;
    private JComboBox<String> filtrStatusKarnetuComboBox;

    public PanelWyboruCzlonka(CzlonekKlubuRepository czlonekKlubuRepository,
                              Runnable onWroc,
                              Consumer<CzlonekKlubu> onWybierzCzlonka,
                              Runnable onAnuluj) {
        if (czlonekKlubuRepository == null) {
            throw new IllegalArgumentException("Repozytorium członków klubu nie może być null.");
        }
        if (onWroc == null) {
            throw new IllegalArgumentException("Akcja powrotu nie może być null.");
        }
        if (onWybierzCzlonka == null) {
            throw new IllegalArgumentException("Akcja wyboru członka nie może być null.");
        }
        if (onAnuluj == null) {
            throw new IllegalArgumentException("Akcja anulowania nie może być null.");
        }

        this.czlonekKlubuRepository = czlonekKlubuRepository;
        this.onWroc = onWroc;
        this.onWybierzCzlonka = onWybierzCzlonka;
        this.onAnuluj = onAnuluj;

        ustawPanel();
        ustawRendererListy();
        zbudujWidok();
    }

    private void ustawPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(GuiStyle.KOLOR_TLA);
    }

    private void ustawRendererListy() {
        czlonkowieList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        czlonkowieList.setCellRenderer((list, czlonek, index, isSelected, cellHasFocus) -> {
            String statusKarnetu = czlonek.czyMaAktywnyKarnet()
                    ? "AKTYWNY"
                    : "BRAK AKTYWNEGO";

            String text = czlonek.getNumerCzlonka()
                    + " | " + czlonek.getImie() + " " + czlonek.getNazwisko()
                    + " | " + czlonek.getEmail()
                    + " | karnet: " + statusKarnetu;

            return GuiStyle.utworzLabelListy(text, isSelected);
        });
    }

    private void zbudujWidok() {
        JPanel panelBazowy = GuiStyle.utworzPanelBazowy("Wybierz członka klubu");

        JPanel centerWrapper = new JPanel(new BorderLayout(0, 12));
        centerWrapper.setBackground(GuiStyle.KOLOR_TLA);

        centerWrapper.add(utworzPanelFiltrow(), BorderLayout.NORTH);
        centerWrapper.add(utworzPanelListy(), BorderLayout.CENTER);

        panelBazowy.add(centerWrapper, BorderLayout.CENTER);
        panelBazowy.add(utworzPanelPrzyciskow(), BorderLayout.SOUTH);

        add(panelBazowy, BorderLayout.CENTER);
    }

    private JPanel utworzPanelFiltrow() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setBackground(GuiStyle.KOLOR_TLA);

        JLabel label = new JLabel("Filtry:");
        wrapper.add(label, BorderLayout.NORTH);

        JPanel filtrPanel = new JPanel(new GridBagLayout());
        filtrPanel.setBackground(GuiStyle.KOLOR_PANELU);
        filtrPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        filtrNumerCzlonkaField = new JTextField(14);
        filtrNazwiskoCzlonkaField = new JTextField(14);
        filtrStatusKarnetuComboBox = new JComboBox<>(new String[]{
                "WSZYSTKIE",
                "AKTYWNY",
                "BRAK_AKTYWNEGO"
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 6, 0, 6);
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        filtrPanel.add(new JLabel("Numer członka:"), gbc);

        gbc.gridx = 1;
        filtrPanel.add(filtrNumerCzlonkaField, gbc);

        gbc.gridx = 2;
        filtrPanel.add(new JLabel("Nazwisko:"), gbc);

        gbc.gridx = 3;
        filtrPanel.add(filtrNazwiskoCzlonkaField, gbc);

        gbc.gridx = 4;
        filtrPanel.add(new JLabel("Status karnetu:"), gbc);

        gbc.gridx = 5;
        filtrPanel.add(filtrStatusKarnetuComboBox, gbc);

        JButton filtrujButton = GuiStyle.utworzPrzycisk("Filtruj", GuiStyle.KOLOR_AKCENT);
        filtrujButton.addActionListener(e -> odswiezCzlonkow());

        gbc.gridx = 6;
        filtrPanel.add(filtrujButton, gbc);

        JButton wyczyscButton = new JButton("Wyczyść");
        wyczyscButton.addActionListener(e -> {
            filtrNumerCzlonkaField.setText("");
            filtrNazwiskoCzlonkaField.setText("");
            filtrStatusKarnetuComboBox.setSelectedItem("WSZYSTKIE");
            odswiezCzlonkow();
        });

        gbc.gridx = 7;
        filtrPanel.add(wyczyscButton, gbc);

        wrapper.add(filtrPanel, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel utworzPanelListy() {
        JPanel listaPanel = new JPanel(new BorderLayout(0, 10));
        listaPanel.setBackground(GuiStyle.KOLOR_TLA);

        listaPanel.add(new JLabel("Dostępni członkowie klubu:"), BorderLayout.NORTH);
        listaPanel.add(GuiStyle.utworzScrollPane(czlonkowieList), BorderLayout.CENTER);

        return listaPanel;
    }

    private JPanel utworzPanelPrzyciskow() {
        JPanel buttons = GuiStyle.utworzPanelPrzyciskow();

        JButton wrocButton = GuiStyle.utworzPrzycisk("Wróć", GuiStyle.KOLOR_WROC);
        wrocButton.addActionListener(e -> onWroc.run());

        JButton odswiezButton = GuiStyle.utworzPrzycisk("Odśwież", GuiStyle.KOLOR_ODSWIEZ);
        odswiezButton.addActionListener(e -> odswiezCzlonkow());

        JButton wybierzButton = GuiStyle.utworzPrzycisk("Wybierz członka", GuiStyle.KOLOR_AKCENT);
        wybierzButton.addActionListener(e -> onWybierzCzlonka.accept(czlonkowieList.getSelectedValue()));

        JButton anulujButton = GuiStyle.utworzPrzycisk("Anuluj", GuiStyle.KOLOR_ANULUJ);
        anulujButton.addActionListener(e -> onAnuluj.run());

        buttons.add(wrocButton);
        buttons.add(odswiezButton);
        buttons.add(wybierzButton);
        buttons.add(anulujButton);

        return buttons;
    }

    // Odświeża listę członków na drugim ekranie.
// Jest wywoływana m.in. po wybraniu sesji,
// żeby przed pokazaniem ekranu członków lista była aktualna.
    public void odswiezCzlonkow() {
        czlonkowieModel.clear();

        String filtrNumer = filtrNumerCzlonkaField == null
                ? ""
                : filtrNumerCzlonkaField.getText().trim().toLowerCase();

        String filtrNazwisko = filtrNazwiskoCzlonkaField == null
                ? ""
                : filtrNazwiskoCzlonkaField.getText().trim().toLowerCase();

        String filtrStatusKarnetu = filtrStatusKarnetuComboBox == null
                ? "WSZYSTKIE"
                : (String) filtrStatusKarnetuComboBox.getSelectedItem();

        List<CzlonekKlubu> czlonkowie = czlonekKlubuRepository.findAll();

        for (CzlonekKlubu czlonek : czlonkowie) {
            if (!czyCzlonekPasujeDoFiltrow(czlonek, filtrNumer, filtrNazwisko, filtrStatusKarnetu)) {
                continue;
            }

            czlonkowieModel.addElement(czlonek);
        }
    }

    public void wyczyscWybor() {
        czlonkowieList.clearSelection();
    }

    private boolean czyCzlonekPasujeDoFiltrow(CzlonekKlubu czlonek,
                                              String filtrNumer,
                                              String filtrNazwisko,
                                              String filtrStatusKarnetu) {
        if (!filtrNumer.isEmpty()
                && !czlonek.getNumerCzlonka().toLowerCase().contains(filtrNumer)) {
            return false;
        }

        if (!filtrNazwisko.isEmpty()
                && !czlonek.getNazwisko().toLowerCase().contains(filtrNazwisko)) {
            return false;
        }

        boolean maAktywnyKarnet = czlonek.czyMaAktywnyKarnet();

        if ("AKTYWNY".equals(filtrStatusKarnetu) && !maAktywnyKarnet) {
            return false;
        }

        if ("BRAK_AKTYWNEGO".equals(filtrStatusKarnetu) && maAktywnyKarnet) {
            return false;
        }

        return true;
    }
}