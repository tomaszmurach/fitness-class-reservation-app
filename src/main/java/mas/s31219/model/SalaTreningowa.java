package mas.s31219.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class SalaTreningowa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String numerSali;

    @NotBlank
    @Column(nullable = false)
    private String nazwa;

    @Min(1)
    @Column(nullable = false)
    private int pojemnosc;

    @Positive
    @Column(nullable = false)
    private double powierzchniaWM2;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "oddzial_klubu_id", nullable = false)
    private OddzialKlubu oddzialKlubu;

    @OneToMany(mappedBy = "salaTreningowa")
    private List<SesjaZajec> sesjeZajec = new ArrayList<>();

    protected SalaTreningowa() {
    }

    SalaTreningowa(String numerSali,
                   String nazwa,
                   int pojemnosc,
                   double powierzchniaWM2,
                   OddzialKlubu oddzialKlubu) {
        setNumerSali(numerSali);
        setNazwa(nazwa);
        setPojemnosc(pojemnosc);
        setPowierzchniaWM2(powierzchniaWM2);
        setOddzialKlubu(oddzialKlubu);
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

    public Long getId() {
        return id;
    }

    public String getNumerSali() {
        return numerSali;
    }

    public void setNumerSali(String numerSali) {
        if (numerSali == null || numerSali.isBlank()) {
            throw new IllegalArgumentException("Numer sali nie może być pusty.");
        }
        this.numerSali = numerSali;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        if (nazwa == null || nazwa.isBlank()) {
            throw new IllegalArgumentException("Nazwa sali nie może być pusta.");
        }
        this.nazwa = nazwa;
    }

    public int getPojemnosc() {
        return pojemnosc;
    }

    public void setPojemnosc(int pojemnosc) {
        if (pojemnosc <= 0) {
            throw new IllegalArgumentException("Pojemność sali musi być dodatnia.");
        }
        this.pojemnosc = pojemnosc;
    }

    public double getPowierzchniaWM2() {
        return powierzchniaWM2;
    }

    public void setPowierzchniaWM2(double powierzchniaWM2) {
        if (powierzchniaWM2 <= 0) {
            throw new IllegalArgumentException("Powierzchnia sali musi być dodatnia.");
        }
        this.powierzchniaWM2 = powierzchniaWM2;
    }

    public OddzialKlubu getOddzialKlubu() {
        return oddzialKlubu;
    }

    void setOddzialKlubu(OddzialKlubu oddzialKlubu) {
        if (oddzialKlubu == null) {
            throw new IllegalArgumentException("Sala treningowa musi należeć do oddziału klubu.");
        }
        this.oddzialKlubu = oddzialKlubu;
    }

    public List<SesjaZajec> getSesjeZajec() {
        return Collections.unmodifiableList(sesjeZajec);
    }

    @Override
    public String toString() {
        return numerSali + " - " + nazwa + " (" + pojemnosc + " miejsc)";
    }
}