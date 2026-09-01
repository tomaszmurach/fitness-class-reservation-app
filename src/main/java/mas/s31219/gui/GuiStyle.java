package mas.s31219.gui;

import javax.swing.*;
import java.awt.*;

public final class GuiStyle {

    private GuiStyle() {
    }

    public static final Color KOLOR_TLA = new Color(240, 240, 245);
    public static final Color KOLOR_PANELU = new Color(170, 170, 170);
    public static final Color KOLOR_NAGLOWKA = new Color(190, 190, 190);
    public static final Color KOLOR_LISTA = new Color(245, 245, 245);
    public static final Color KOLOR_LISTA_ZAZNACZONA = new Color(205, 225, 250);

    public static final Color KOLOR_AKCENT = new Color(0, 190, 170);
    public static final Color KOLOR_ODSWIEZ = new Color(95, 80, 230);
    public static final Color KOLOR_ANULUJ = new Color(245, 60, 60);
    public static final Color KOLOR_WROC = new Color(180, 130, 85);
    public static final Color KOLOR_POTWIERDZ = new Color(40, 200, 90);

    public static JPanel utworzPanelBazowy(String naglowek) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(KOLOR_TLA);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel(naglowek);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        title.setOpaque(true);
        title.setBackground(KOLOR_NAGLOWKA);
        title.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        panel.add(title, BorderLayout.NORTH);

        return panel;
    }

    public static JButton utworzPrzycisk(String tekst, Color kolor) {
        JButton button = new JButton(tekst);
        button.setFocusPainted(false);
        button.setBackground(kolor);
        button.setForeground(Color.BLACK);
        button.setPreferredSize(new Dimension(130, 32));

        return button;
    }

    public static JLabel utworzLabelListy(String text, boolean isSelected) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(9, 10, 9, 10));

        if (isSelected) {
            label.setBackground(KOLOR_LISTA_ZAZNACZONA);
        } else {
            label.setBackground(KOLOR_LISTA);
        }

        return label;
    }

    public static JPanel utworzPanelPrzyciskow() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(KOLOR_TLA);

        return buttons;
    }

    public static JScrollPane utworzScrollPane(JComponent component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(120, 135, 155)));

        return scrollPane;
    }
}