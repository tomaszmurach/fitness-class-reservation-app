package mas.s31219.gui;

import mas.s31219.model.Rezerwacja;
import mas.s31219.model.SesjaZajec;
import mas.s31219.model.StatusSesji;
import mas.s31219.repository.SesjaZajecRepository;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class PanelWyboruSesji extends JPanel {

    private final SesjaZajecRepository sesjaZajecRepository;
    private final Consumer<SesjaZajec> onWybierzSesje;
    private final Runnable onZamknij;

    // Model danych lewej listy.
// Przechowuje obiekty SesjaZajec pobrane z bazy.
    private final DefaultListModel<SesjaZajec> sesjeModel = new DefaultListModel<>();

    // Lista GUI wyświetlająca sesje.
// Lista trzyma obiekty SesjaZajec, a ich wygląd tekstowy ustala renderer.
    private final JList<SesjaZajec> sesjeList = new JList<>(sesjeModel);

    // Model danych prawej listy.
// Przechowuje rezerwacje powiązane z aktualnie wybraną sesją.
    private final DefaultListModel<Rezerwacja> rezerwacjeModel = new DefaultListModel<>();

    // Lista GUI wyświetlająca rezerwacje wybranej sesji.
// Elementy są obiektami Rezerwacja, a tekst na ekranie tworzy renderer.
    private final JList<Rezerwacja> rezerwacjeList = new JList<>(rezerwacjeModel);

    private JTextField filtrNazwaField;
    private JTextField filtrDataField;
    private JComboBox<String> filtrStatusComboBox;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Panel pierwszego kroku procesu.
// Odpowiada za wyświetlenie dostępnych sesji zajęć.
    public PanelWyboruSesji(SesjaZajecRepository sesjaZajecRepository,
                            Consumer<SesjaZajec> onWybierzSesje,
                            Runnable onZamknij) {

        // Repozytorium jest wymagane, bo panel pobiera z niego sesje do lewej listy.
        if (sesjaZajecRepository == null) {
            throw new IllegalArgumentException("Repozytorium sesji zajęć nie może być null.");
        }

        // Callback wyboru sesji jest wymagany,
        // ale w tym przepływie jeszcze go nie używamy.
        // Będzie potrzebny w kolejnym kroku po kliknięciu "Wybierz sesję".
        if (onWybierzSesje == null) {
            throw new IllegalArgumentException("Akcja wyboru sesji nie może być null.");
        }

        // Callback zamknięcia okna.
        if (onZamknij == null) {
            throw new IllegalArgumentException("Akcja zamknięcia nie może być null.");
        }

        this.sesjaZajecRepository = sesjaZajecRepository;
        this.onWybierzSesje = onWybierzSesje;
        this.onZamknij = onZamknij;

        // Ustawienie wyglądu panelu i zbudowanie komponentów GUI.
        ustawPanel();
        ustawRendereryList();
        zbudujWidok();
    }

    private void ustawPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(GuiStyle.KOLOR_TLA);
    }

    private void ustawRendereryList() {
        sesjeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rezerwacjeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Renderer określa, jak obiekt SesjaZajec ma być pokazany w JList.
// W modelu listy trzymamy cały obiekt, ale użytkownik widzi czytelny opis.
        sesjeList.setCellRenderer((list, sesja, index, isSelected, cellHasFocus) -> {
            String text = sesja.getZajeciaGrupowe().getNazwa()
                    + " | " + sesja.getDataCzasRozpoczecia().format(formatter)
                    + " | " + sesja.getTrener().getImie() + " " + sesja.getTrener().getNazwisko()
                    + " | sala: " + sesja.getSalaTreningowa().getNumerSali()
                    + " | status: " + sesja.getStatus()
                    + " | wolne miejsca: " + sesja.getDostepneMiejsca();

            return GuiStyle.utworzLabelListy(text, isSelected);
        });

        // Renderer prawej listy.
// Określa, jak obiekt Rezerwacja ma być pokazany użytkownikowi.
// Dane członka pobieramy przez rezerwacja.getCzlonekKlubu(),
// bo Rezerwacja jest powiązana z CzlonekKlubu.
        rezerwacjeList.setCellRenderer((list, rezerwacja, index, isSelected, cellHasFocus) -> {
            String text = rezerwacja.getCzlonekKlubu().getNumerCzlonka()
                    + " | " + rezerwacja.getCzlonekKlubu().getImie()
                    + " " + rezerwacja.getCzlonekKlubu().getNazwisko()
                    + " | " + rezerwacja.getStatus()
                    + " | " + rezerwacja.getDataRezerwacji().format(formatter);

            return GuiStyle.utworzLabelListy(text, isSelected);
        });
    }

    private void zbudujWidok() {
        JPanel panelBazowy = GuiStyle.utworzPanelBazowy("Wybierz sesję zajęć");

        JPanel centerWrapper = new JPanel(new BorderLayout(0, 12));
        centerWrapper.setBackground(GuiStyle.KOLOR_TLA);

        centerWrapper.add(utworzPanelFiltrow(), BorderLayout.NORTH);
        centerWrapper.add(utworzPanelList(), BorderLayout.CENTER);

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

        filtrNazwaField = new JTextField(18);
        filtrDataField = new JTextField(13);
        // ComboBox statusu korzysta z wartości enum StatusSesji.
// Domyślnie pokazujemy sesje zaplanowane, bo tylko takie są normalnie dostępne do rezerwacji.
        filtrStatusComboBox = new JComboBox<>(new String[]{
                "WSZYSTKIE",
                StatusSesji.ZAPLANOWANA.name(),
                StatusSesji.ANULOWANA.name(),
                StatusSesji.ZAKONCZONA.name()
        });
        filtrStatusComboBox.setSelectedItem(StatusSesji.ZAPLANOWANA.name());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 6, 0, 6);
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        filtrPanel.add(new JLabel("Nazwa:"), gbc);

        gbc.gridx = 1;
        filtrPanel.add(filtrNazwaField, gbc);

        gbc.gridx = 2;
        filtrPanel.add(new JLabel("Data:"), gbc);

        gbc.gridx = 3;
        filtrPanel.add(filtrDataField, gbc);

        gbc.gridx = 4;
        filtrPanel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 5;
        filtrPanel.add(filtrStatusComboBox, gbc);

        JButton filtrujButton = GuiStyle.utworzPrzycisk("Filtruj", GuiStyle.KOLOR_AKCENT);
        filtrujButton.addActionListener(e -> {
            odswiezSesje();
            rezerwacjeModel.clear();
        });

        gbc.gridx = 6;
        filtrPanel.add(filtrujButton, gbc);

        JButton wyczyscButton = new JButton("Wyczyść");
        wyczyscButton.addActionListener(e -> {
            filtrNazwaField.setText("");
            filtrDataField.setText("");
            filtrStatusComboBox.setSelectedItem(StatusSesji.ZAPLANOWANA.name());
            odswiezSesje();
            rezerwacjeModel.clear();
        });

        gbc.gridx = 7;
        filtrPanel.add(wyczyscButton, gbc);

        wrapper.add(filtrPanel, BorderLayout.CENTER);

        return wrapper;
    }

    private JSplitPane utworzPanelList() {
        JPanel sesjePanel = new JPanel(new BorderLayout(0, 10));
        sesjePanel.setBackground(GuiStyle.KOLOR_TLA);
        sesjePanel.add(new JLabel("Dostępne sesje zajęć:"), BorderLayout.NORTH);
        sesjePanel.add(GuiStyle.utworzScrollPane(sesjeList), BorderLayout.CENTER);

        JPanel rezerwacjePanel = new JPanel(new BorderLayout(0, 10));
        rezerwacjePanel.setBackground(GuiStyle.KOLOR_TLA);
        rezerwacjePanel.add(new JLabel("Rezerwacje wybranej sesji:"), BorderLayout.NORTH);
        rezerwacjePanel.add(GuiStyle.utworzScrollPane(rezerwacjeList), BorderLayout.CENTER);

        // Panel z dwiema listami:
// po lewej dostępne sesje zajęć,
// po prawej rezerwacje powiązane z aktualnie wybraną sesją.
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                sesjePanel,
                rezerwacjePanel
        );
        splitPane.setResizeWeight(0.68);
        splitPane.setDividerLocation(800);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(null);

        // Listener reaguje na kliknięcie/zaznaczenie sesji na lewej liście.
// Po wybraniu sesji odświeża prawą listę rezerwacji.
        sesjeList.addListSelectionListener(e -> {
            // Zabezpieczenie przed wielokrotnym wykonaniem w trakcie zmiany zaznaczenia.
            if (!e.getValueIsAdjusting()) {

                // Pobieramy obiekt SesjaZajec zaznaczony na lewej liście.
                SesjaZajec sesja = sesjeList.getSelectedValue();

                // Jeżeli użytkownik faktycznie wybrał sesję,
                // pokazujemy jej rezerwacje po prawej stronie.
                if (sesja != null) {
                    odswiezRezerwacjeSesjiPrzezAsocjacje(sesja);
                }
            }
        });

        return splitPane;
    }

    private JPanel utworzPanelPrzyciskow() {
        JPanel buttons = GuiStyle.utworzPanelPrzyciskow();

        JButton odswiezButton = GuiStyle.utworzPrzycisk("Odśwież", GuiStyle.KOLOR_ODSWIEZ);
        odswiezButton.addActionListener(e -> {
            odswiezSesje();
            rezerwacjeModel.clear();
        });

        // Przycisk kończy pierwszy krok procesu.
// Pobiera aktualnie zaznaczoną SesjaZajec z lewej listy
// i przekazuje ją do RezerwacjaFrame przez callback onWybierzSesje.
// Panel nie przełącza sam ekranu - robi to główne okno RezerwacjaFrame.
        JButton wybierzButton = GuiStyle.utworzPrzycisk("Wybierz sesję", GuiStyle.KOLOR_AKCENT);
        wybierzButton.addActionListener(e -> onWybierzSesje.accept(sesjeList.getSelectedValue()));

        JButton zamknijButton = GuiStyle.utworzPrzycisk("Zamknij", GuiStyle.KOLOR_ANULUJ);
        zamknijButton.addActionListener(e -> onZamknij.run());

        buttons.add(odswiezButton);
        buttons.add(wybierzButton);
        buttons.add(zamknijButton);

        return buttons;
    }

    // Odświeża lewą listę sesji.
// Jest wywoływana przy pokazaniu pierwszego ekranu,
// po kliknięciu "Odśwież" oraz po zmianie/wyczyszczeniu filtrów.
    public void odswiezSesje() {

        // Czyścimy aktualny model listy, żeby nie dublować wyników.
        sesjeModel.clear();

        // Pobieramy aktualne wartości filtrów z pól GUI.
        String filtrNazwa = filtrNazwaField == null
                ? ""
                : filtrNazwaField.getText().trim().toLowerCase();

        String filtrData = filtrDataField == null
                ? ""
                : filtrDataField.getText().trim();

        String filtrStatus = filtrStatusComboBox == null
                ? StatusSesji.ZAPLANOWANA.name()
                : (String) filtrStatusComboBox.getSelectedItem();

        // Pobranie sesji z bazy przez repozytorium.
        // Repozytorium wykonuje zapytanie JPQL i zwraca obiekty SesjaZajec.
        List<SesjaZajec> sesje = sesjaZajecRepository.findAllOrderByDataRozpoczecia();

        // Filtrowanie odbywa się po stronie GUI.
        // Do modelu listy trafiają tylko sesje spełniające aktualne filtry.
        for (SesjaZajec sesja : sesje) {
            if (!czySesjaPasujeDoFiltrow(sesja, filtrNazwa, filtrData, filtrStatus)) {
                continue;
            }

            // Dodanie obiektu SesjaZajec do modelu JList.
            // Dzięki rendererowi użytkownik widzi czytelny opis sesji.
            sesjeModel.addElement(sesja);
        }
    }

    public void wyczyscWybor() {
        sesjeList.clearSelection();
        rezerwacjeModel.clear();
    }

    // Sprawdza, czy konkretna sesja spełnia aktualne filtry z GUI.
// To jest logika widoku, nie logika biznesowa.
// Nie zmienia danych w bazie, tylko decyduje, czy pokazać sesję na liście.
    private boolean czySesjaPasujeDoFiltrow(SesjaZajec sesja,
                                            String filtrNazwa,
                                            String filtrData,
                                            String filtrStatus) {

        // Filtr nazwy: sprawdzamy nazwę zajęć grupowych powiązanych z sesją.
        if (!filtrNazwa.isEmpty()
                && !sesja.getZajeciaGrupowe().getNazwa().toLowerCase().contains(filtrNazwa)) {
            return false;
        }

        // Filtr daty: porównujemy tekstowo datę rozpoczęcia sesji.
        if (!filtrData.isEmpty()
                && !sesja.getDataCzasRozpoczecia().toLocalDate().toString().contains(filtrData)) {
            return false;
        }

        // Filtr statusu: jeśli wybrano WSZYSTKIE, nie filtrujemy po statusie.
        // W przeciwnym razie status sesji musi być równy wybranej wartości.
        if (filtrStatus != null
                && !"WSZYSTKIE".equals(filtrStatus)
                && !sesja.getStatus().name().equals(filtrStatus)) {
            return false;
        }

        return true;
    }

    // Odświeża prawą listę rezerwacji dla wybranej sesji.
// To jest kluczowa metoda dla wymagania GUI z asocjacją "wiele".
// Rezerwacje są pobierane przez zdefiniowaną asocjację SesjaZajec -> Rezerwacja,
// czyli przez sesja.getRezerwacje(), a nie przez osobne zapytanie SQL/JPQL.
    private void odswiezRezerwacjeSesjiPrzezAsocjacje(SesjaZajec sesja) {
        // Czyścimy poprzednie rezerwacje z prawej listy.
        rezerwacjeModel.clear();

        // Pobieramy rezerwacje bezpośrednio z wybranej sesji przez asocjację.
        for (Rezerwacja rezerwacja : sesja.getRezerwacje()) {

            // Dodajemy każdą rezerwację do modelu prawej JList.
            rezerwacjeModel.addElement(rezerwacja);
        }
    }
}