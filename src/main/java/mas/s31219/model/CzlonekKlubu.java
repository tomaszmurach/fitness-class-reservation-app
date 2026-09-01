package mas.s31219.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class CzlonekKlubu extends Osoba {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String numerCzlonka;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataDolaczenia;

    @Min(0)
    @Column(nullable = false)
    private int punktyLojalnosciowe;

    @Column
    private String notatkiZdrowotne;

    @ElementCollection
    @CollectionTable(
            name = "czlonek_klubu_kontakty_awaryjne",
            joinColumns = @JoinColumn(name = "czlonek_klubu_id")
    )
    @Column(name = "kontakt_awaryjny", nullable = false)
    private List<String> kontaktyAwaryjne = new ArrayList<>();

    @OneToMany(mappedBy = "czlonekKlubu")
    private List<Karnet> karnety = new ArrayList<>();

    // Historia rezerwacji członka.
// Jest to druga strona relacji CzlonekKlubu -> Rezerwacja.
// W tym przepływie GUI pokazuje rezerwacje od strony SesjaZajec,
// ale model pozwala też zobaczyć rezerwacje konkretnego członka.
    @OneToMany(mappedBy = "czlonekKlubu")
    private List<Rezerwacja> rezerwacje = new ArrayList<>();

    protected CzlonekKlubu() {
    }

    public CzlonekKlubu(String imie,
                        String nazwisko,
                        LocalDate dataUrodzenia,
                        String numerTelefonu,
                        String email,
                        String numerCzlonka,
                        LocalDate dataDolaczenia,
                        int punktyLojalnosciowe,
                        String notatkiZdrowotne) {
        super(imie, nazwisko, dataUrodzenia, numerTelefonu, email);
        setNumerCzlonka(numerCzlonka);
        setDataDolaczenia(dataDolaczenia);
        setPunktyLojalnosciowe(punktyLojalnosciowe);
        setNotatkiZdrowotne(notatkiZdrowotne);
    }

    public int obliczCzasCzlonkostwaWMiesiacach() {
        return (int) ChronoUnit.MONTHS.between(dataDolaczenia, LocalDate.now());
    }

    public void dodajPunktyLojalnosciowe(int punkty) {
        if (punkty <= 0) {
            throw new IllegalArgumentException("Liczba dodawanych punktów musi być dodatnia.");
        }
        this.punktyLojalnosciowe += punkty;
    }

    public boolean czyMaAktywnyKarnet() {
        return karnety.stream()
                .anyMatch(karnet ->
                        karnet.isAktualny()
                                && karnet.getStatusKarnetu() == StatusKarnetu.AKTYWNY
                );
    }

    public KarnetAktywny pobierzAktywnyKarnet() {
        return karnety.stream()
                .filter(Karnet::isAktualny)
                .filter(karnet -> karnet.getStatusKarnetu() == StatusKarnetu.AKTYWNY)
                .filter(KarnetAktywny.class::isInstance)
                .map(KarnetAktywny.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Członek klubu nie posiada aktywnego karnetu."));
    }

    public Karnet pobierzAktualnyKarnet() {
        return karnety.stream()
                .filter(Karnet::isAktualny)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Członek klubu nie posiada aktualnego karnetu."));
    }

    void dodajKarnetDoHistorii(Karnet karnet) {
        if (karnet == null) {
            throw new IllegalArgumentException("Karnet jest wymagany.");
        }

        if (karnet.isAktualny()) {
            for (Karnet istniejacyKarnet : karnety) {
                if (istniejacyKarnet != karnet && istniejacyKarnet.isAktualny()) {
                    istniejacyKarnet.oznaczJakoNieaktualny();
                }
            }
        }

        if (!karnety.contains(karnet)) {
            karnety.add(karnet);
        }
    }

    void usunKarnetZHistorii(Karnet karnet) {
        karnety.remove(karnet);
    }

    void dodajRezerwacjeDoHistorii(Rezerwacja rezerwacja) {
        if (rezerwacja == null) {
            throw new IllegalArgumentException("Rezerwacja jest wymagana.");
        }

        if (!rezerwacje.contains(rezerwacja)) {
            rezerwacje.add(rezerwacja);
        }
    }

    void usunRezerwacjeZHistorii(Rezerwacja rezerwacja) {
        rezerwacje.remove(rezerwacja);
    }

    public String getNumerCzlonka() {
        return numerCzlonka;
    }

    public void setNumerCzlonka(String numerCzlonka) {
        if (numerCzlonka == null || numerCzlonka.isBlank()) {
            throw new IllegalArgumentException("Numer członka nie może być pusty.");
        }
        this.numerCzlonka = numerCzlonka;
    }

    public LocalDate getDataDolaczenia() {
        return dataDolaczenia;
    }

    public void setDataDolaczenia(LocalDate dataDolaczenia) {
        if (dataDolaczenia == null) {
            throw new IllegalArgumentException("Data dołączenia jest wymagana.");
        }
        if (dataDolaczenia.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data dołączenia nie może być z przyszłości.");
        }
        this.dataDolaczenia = dataDolaczenia;
    }

    public int getPunktyLojalnosciowe() {
        return punktyLojalnosciowe;
    }

    public void setPunktyLojalnosciowe(int punktyLojalnosciowe) {
        if (punktyLojalnosciowe < 0) {
            throw new IllegalArgumentException("Punkty lojalnościowe nie mogą być ujemne.");
        }
        this.punktyLojalnosciowe = punktyLojalnosciowe;
    }

    public String getNotatkiZdrowotne() {
        return notatkiZdrowotne;
    }

    public void setNotatkiZdrowotne(String notatkiZdrowotne) {
        this.notatkiZdrowotne = notatkiZdrowotne;
    }

    public List<String> getKontaktyAwaryjne() {
        return Collections.unmodifiableList(kontaktyAwaryjne);
    }

    public void dodajKontaktAwaryjny(String kontaktAwaryjny) {
        if (kontaktAwaryjny == null || kontaktAwaryjny.isBlank()) {
            throw new IllegalArgumentException("Kontakt awaryjny nie może być pusty.");
        }
        kontaktyAwaryjne.add(kontaktAwaryjny);
    }

    public void usunKontaktAwaryjny(String kontaktAwaryjny) {
        kontaktyAwaryjne.remove(kontaktAwaryjny);
    }

    public List<Karnet> getKarnety() {
        return Collections.unmodifiableList(karnety);
    }

    public List<Rezerwacja> getRezerwacje() {
        return Collections.unmodifiableList(rezerwacje);
    }
}