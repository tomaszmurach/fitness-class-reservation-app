package mas.s31219.gui;

import javax.swing.*;
import java.awt.*;

public class PanelBledu extends JPanel {

    private final Runnable onWroc;
    private final Runnable onOk;

    private final JTextArea bladArea = new JTextArea();

    public PanelBledu(Runnable onWroc, Runnable onOk) {
        if (onWroc == null) {
            throw new IllegalArgumentException("Akcja powrotu nie może być null.");
        }
        if (onOk == null) {
            throw new IllegalArgumentException("Akcja OK nie może być null.");
        }

        this.onWroc = onWroc;
        this.onOk = onOk;

        ustawPanel();
        zbudujWidok();
    }

    private void ustawPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(GuiStyle.KOLOR_TLA);
    }

    private void zbudujWidok() {
        JPanel panelBazowy = GuiStyle.utworzPanelBazowy("Nie można utworzyć rezerwacji");

        JPanel ramkaPanel = new JPanel(new BorderLayout(0, 10));
        ramkaPanel.setBackground(GuiStyle.KOLOR_PANELU);
        ramkaPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel przyczynaLabel = new JLabel("Przyczyna:");

        bladArea.setEditable(false);
        bladArea.setLineWrap(true);
        bladArea.setWrapStyleWord(true);
        bladArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        bladArea.setBackground(Color.WHITE);
        bladArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ramkaPanel.add(przyczynaLabel, BorderLayout.NORTH);
        ramkaPanel.add(new JScrollPane(bladArea), BorderLayout.CENTER);

        panelBazowy.add(ramkaPanel, BorderLayout.CENTER);
        panelBazowy.add(utworzPanelPrzyciskow(), BorderLayout.SOUTH);

        add(panelBazowy, BorderLayout.CENTER);
    }

    private JPanel utworzPanelPrzyciskow() {
        JPanel buttons = GuiStyle.utworzPanelPrzyciskow();

        JButton wrocButton = GuiStyle.utworzPrzycisk("Wróć", GuiStyle.KOLOR_WROC);
        wrocButton.addActionListener(e -> onWroc.run());

        JButton okButton = GuiStyle.utworzPrzycisk("OK", GuiStyle.KOLOR_POTWIERDZ);
        okButton.addActionListener(e -> onOk.run());

        buttons.add(wrocButton);
        buttons.add(okButton);

        return buttons;
    }

    public void wyswietlKomunikat(String komunikat) {
        if (komunikat == null || komunikat.isBlank()) {
            bladArea.setText("Operacja nie może zostać wykonana.");
        } else {
            bladArea.setText(komunikat);
        }

        bladArea.setCaretPosition(0);
    }
}