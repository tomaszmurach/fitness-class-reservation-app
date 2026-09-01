package mas.s31219.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Trener extends Osoba {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String numerTrenera;

    @NotBlank
    @Column(nullable = false)
    private String specjalizacja;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal stawkaGodzinowa;

    @ElementCollection
    @CollectionTable(
            name = "trener_certyfikaty",
            joinColumns = @JoinColumn(name = "trener_id")
    )
    @Column(name = "certyfikat", nullable = false)
    private List<String> certyfikaty = new ArrayList<>();

    @OneToMany(mappedBy = "trener")
    private List<SesjaZajec> sesjeZajec = new ArrayList<>();

    protected Trener() {
    }

    public Trener(String imie,
                  String nazwisko,
                  LocalDate dataUrodzenia,
                  String numerTelefonu,
                  String email,
                  String numerTrenera,
                  String specjalizacja,
                  BigDecimal stawkaGodzinowa) {
        super(imie, nazwisko, dataUrodzenia, numerTelefonu, email);
        setNumerTrenera(numerTrenera);
        setSpecjalizacja(specjalizacja);
        setStawkaGodzinowa(stawkaGodzinowa);
    }

    public BigDecimal obliczWynagrodzenieZaTrening(int godziny) {
        if (godziny <= 0) {
            throw new IllegalArgumentException("Liczba godzin musi być dodatnia.");
        }
        return stawkaGodzinowa.multiply(BigDecimal.valueOf(godziny));
    }

    void dodajSesjeZajec(SesjaZajec sesjaZajec) {
        if (sesjaZajec == null) {
            throw new IllegalArgumentException("Sesja zajęć jest wymagana.");
        }

        if (!sesjeZajec.contains(sesjaZajec)) {
            sesjeZajec.add(sesjaZajec);
        }
    }

    void usunSesjeZajec(SesjaZajec sesjaZajec) {
        sesjeZajec.remove(sesjaZajec);
    }

    public String getNumerTrenera() {
        return numerTrenera;
    }

    public void setNumerTrenera(String numerTrenera) {
        if (numerTrenera == null || numerTrenera.isBlank()) {
            throw new IllegalArgumentException("Numer trenera nie może być pusty.");
        }
        this.numerTrenera = numerTrenera;
    }

    public String getSpecjalizacja() {
        return specjalizacja;
    }

    public void setSpecjalizacja(String specjalizacja) {
        if (specjalizacja == null || specjalizacja.isBlank()) {
            throw new IllegalArgumentException("Specjalizacja nie może być pusta.");
        }
        this.specjalizacja = specjalizacja;
    }

    public BigDecimal getStawkaGodzinowa() {
        return stawkaGodzinowa;
    }

    public void setStawkaGodzinowa(BigDecimal stawkaGodzinowa) {
        if (stawkaGodzinowa == null) {
            throw new IllegalArgumentException("Stawka godzinowa jest wymagana.");
        }
        if (stawkaGodzinowa.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Stawka godzinowa nie może być ujemna.");
        }
        this.stawkaGodzinowa = stawkaGodzinowa;
    }

    public List<String> getCertyfikaty() {
        return Collections.unmodifiableList(certyfikaty);
    }

    public void dodajCertyfikat(String certyfikat) {
        if (certyfikat == null || certyfikat.isBlank()) {
            throw new IllegalArgumentException("Certyfikat nie może być pusty.");
        }
        certyfikaty.add(certyfikat);
    }

    public void usunCertyfikat(String certyfikat) {
        certyfikaty.remove(certyfikat);
    }

    public List<SesjaZajec> getSesjeZajec() {
        return Collections.unmodifiableList(sesjeZajec);
    }
}