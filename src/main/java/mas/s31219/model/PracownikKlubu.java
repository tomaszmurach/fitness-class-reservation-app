package mas.s31219.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class PracownikKlubu extends Osoba {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String identyfikatorPracownika;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataZatrudnienia;

    @NotBlank
    @Column(nullable = false)
    private String stanowisko;

    @NotBlank
    @Column(nullable = false)
    private String poziomUprawnien;

    @OneToMany(mappedBy = "zarejestrowanaPrzez")
    private List<Platnosc> zarejestrowanePlatnosci = new ArrayList<>();

    @OneToMany(mappedBy = "zaplanowanePrzez")
    private List<SesjaZajec> zaplanowaneSesje = new ArrayList<>();

    protected PracownikKlubu() {
    }

    public PracownikKlubu(String imie,
                          String nazwisko,
                          LocalDate dataUrodzenia,
                          String numerTelefonu,
                          String email,
                          String identyfikatorPracownika,
                          LocalDate dataZatrudnienia,
                          String stanowisko,
                          String poziomUprawnien) {
        super(imie, nazwisko, dataUrodzenia, numerTelefonu, email);
        setIdentyfikatorPracownika(identyfikatorPracownika);
        setDataZatrudnienia(dataZatrudnienia);
        setStanowisko(stanowisko);
        setPoziomUprawnien(poziomUprawnien);
    }

    void dodajZarejestrowanaPlatnosc(Platnosc platnosc) {
        if (platnosc == null) {
            throw new IllegalArgumentException("Płatność jest wymagana.");
        }

        if (!zarejestrowanePlatnosci.contains(platnosc)) {
            zarejestrowanePlatnosci.add(platnosc);
        }
    }

    void usunZarejestrowanaPlatnosc(Platnosc platnosc) {
        zarejestrowanePlatnosci.remove(platnosc);
    }

    void dodajZaplanowanaSesje(SesjaZajec sesjaZajec) {
        if (sesjaZajec == null) {
            throw new IllegalArgumentException("Sesja zajęć jest wymagana.");
        }

        if (!zaplanowaneSesje.contains(sesjaZajec)) {
            zaplanowaneSesje.add(sesjaZajec);
        }
    }

    void usunZaplanowanaSesje(SesjaZajec sesjaZajec) {
        zaplanowaneSesje.remove(sesjaZajec);
    }

    public String getIdentyfikatorPracownika() {
        return identyfikatorPracownika;
    }

    public void setIdentyfikatorPracownika(String identyfikatorPracownika) {
        if (identyfikatorPracownika == null || identyfikatorPracownika.isBlank()) {
            throw new IllegalArgumentException("Identyfikator pracownika nie może być pusty.");
        }
        this.identyfikatorPracownika = identyfikatorPracownika;
    }

    public LocalDate getDataZatrudnienia() {
        return dataZatrudnienia;
    }

    public void setDataZatrudnienia(LocalDate dataZatrudnienia) {
        if (dataZatrudnienia == null) {
            throw new IllegalArgumentException("Data zatrudnienia jest wymagana.");
        }
        if (dataZatrudnienia.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data zatrudnienia nie może być z przyszłości.");
        }
        this.dataZatrudnienia = dataZatrudnienia;
    }

    public String getStanowisko() {
        return stanowisko;
    }

    public void setStanowisko(String stanowisko) {
        if (stanowisko == null || stanowisko.isBlank()) {
            throw new IllegalArgumentException("Stanowisko nie może być puste.");
        }
        this.stanowisko = stanowisko;
    }

    public String getPoziomUprawnien() {
        return poziomUprawnien;
    }

    public void setPoziomUprawnien(String poziomUprawnien) {
        if (poziomUprawnien == null || poziomUprawnien.isBlank()) {
            throw new IllegalArgumentException("Poziom uprawnień nie może być pusty.");
        }
        this.poziomUprawnien = poziomUprawnien;
    }

    public List<Platnosc> getZarejestrowanePlatnosci() {
        return Collections.unmodifiableList(zarejestrowanePlatnosci);
    }

    public List<SesjaZajec> getZaplanowaneSesje() {
        return Collections.unmodifiableList(zaplanowaneSesje);
    }
}