package mas.s31219.gui;

import mas.s31219.model.Rezerwacja;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class PanelSukcesu extends JPanel {

    private final Runnable onOk;

    private final JPanel danePanel = new JPanel(new GridLayout(6, 1, 8, 8));
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PanelSukcesu(Runnable onOk) {
        if (onOk == null) {
            throw new IllegalArgumentException("Akcja OK nie może być null.");
        }

        this.onOk = onOk;

        ustawPanel();
        zbudujWidok();
    }

    private void ustawPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(GuiStyle.KOLOR_TLA);
    }

    private void zbudujWidok() {
        JPanel panelBazowy = GuiStyle.utworzPanelBazowy("Rezerwacja została utworzona");

        JPanel ramkaPanel = new JPanel(new BorderLayout(0, 10));
        ramkaPanel.setBackground(GuiStyle.KOLOR_PANELU);
        ramkaPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel daneRezerwacjiLabel = new JLabel("Dane rezerwacji:");

        danePanel.setBackground(Color.WHITE);
        danePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ramkaPanel.add(daneRezerwacjiLabel, BorderLayout.NORTH);
        ramkaPanel.add(danePanel, BorderLayout.CENTER);

        panelBazowy.add(ramkaPanel, BorderLayout.CENTER);
        panelBazowy.add(utworzPanelPrzyciskow(), BorderLayout.SOUTH);

        add(panelBazowy, BorderLayout.CENTER);
    }

    private JPanel utworzPanelPrzyciskow() {
        JPanel buttons = GuiStyle.utworzPanelPrzyciskow();

        JButton okButton = GuiStyle.utworzPrzycisk("OK", GuiStyle.KOLOR_POTWIERDZ);
        okButton.addActionListener(e -> onOk.run());

        buttons.add(okButton);

        return buttons;
    }

    public void wyswietlDane(Rezerwacja rezerwacja) {
        if (rezerwacja == null) {
            throw new IllegalArgumentException("Rezerwacja nie może być null.");
        }

        danePanel.removeAll();

        danePanel.add(new JLabel("Status rezerwacji: " + rezerwacja.getStatus()));
        danePanel.add(new JLabel("Data rezerwacji: " + rezerwacja.getDataRezerwacji().format(formatter)));
        danePanel.add(new JLabel("Nazwa zajęć: " + rezerwacja.getSesjaZajec().getZajeciaGrupowe().getNazwa()));
        danePanel.add(new JLabel("Termin: " + rezerwacja.getSesjaZajec().getDataCzasRozpoczecia().format(formatter)));
        danePanel.add(new JLabel("Członek klubu: "
                + rezerwacja.getCzlonekKlubu().getImie()
                + " "
                + rezerwacja.getCzlonekKlubu().getNazwisko()));
        danePanel.add(new JLabel("Dostępne miejsca po rezerwacji: "
                + rezerwacja.getSesjaZajec().getDostepneMiejsca()));

        danePanel.revalidate();
        danePanel.repaint();
    }
}