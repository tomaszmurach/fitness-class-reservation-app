package mas.s31219.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class OddzialKlubu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String nazwa;

    @NotBlank
    @Column(nullable = false)
    private String adres;

    @NotBlank
    @Column(nullable = false)
    private String numerTelefonu;

    @NotBlank
    @Column(nullable = false)
    private String godzinyOtwarcia;

    @OneToMany(
            mappedBy = "oddzialKlubu",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SalaTreningowa> sale = new ArrayList<>();

    protected OddzialKlubu() {
    }

    public OddzialKlubu(String nazwa,
                        String adres,
                        String numerTelefonu,
                        String godzinyOtwarcia) {
        setNazwa(nazwa);
        setAdres(adres);
        setNumerTelefonu(numerTelefonu);
        setGodzinyOtwarcia(godzinyOtwarcia);
    }

    public SalaTreningowa dodajSale(String numerSali,
                                    String nazwa,
                                    int pojemnosc,
                                    double powierzchniaWM2) {
        SalaTreningowa sala = new SalaTreningowa(
                numerSali,
                nazwa,
                pojemnosc,
                powierzchniaWM2,
                this
        );

        sale.add(sala);
        return sala;
    }

    public void usunSale(SalaTreningowa sala) {
        if (sala == null) {
            throw new IllegalArgumentException("Sala treningowa jest wymagana.");
        }

        if (!sale.contains(sala)) {
            throw new IllegalArgumentException("Podana sala nie należy do tego oddziału.");
        }

        sale.remove(sala);
    }

    public Long getId() {
        return id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        if (nazwa == null || nazwa.isBlank()) {
            throw new IllegalArgumentException("Nazwa oddziału nie może być pusta.");
        }
        this.nazwa = nazwa;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        if (adres == null || adres.isBlank()) {
            throw new IllegalArgumentException("Adres oddziału nie może być pusty.");
        }
        this.adres = adres;
    }

    public String getNumerTelefonu() {
        return numerTelefonu;
    }

    public void setNumerTelefonu(String numerTelefonu) {
        if (numerTelefonu == null || numerTelefonu.isBlank()) {
            throw new IllegalArgumentException("Numer telefonu oddziału nie może być pusty.");
        }
        this.numerTelefonu = numerTelefonu;
    }

    public String getGodzinyOtwarcia() {
        return godzinyOtwarcia;
    }

    public void setGodzinyOtwarcia(String godzinyOtwarcia) {
        if (godzinyOtwarcia == null || godzinyOtwarcia.isBlank()) {
            throw new IllegalArgumentException("Godziny otwarcia nie mogą być puste.");
        }
        this.godzinyOtwarcia = godzinyOtwarcia;
    }

    public List<SalaTreningowa> getSale() {
        return Collections.unmodifiableList(sale);
    }

    @Override
    public String toString() {
        return nazwa + " - " + adres;
    }
}