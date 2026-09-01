package mas.s31219.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class ZajeciaGrupowe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String nazwa;

    @NotBlank
    @Column(nullable = false)
    private String opis;

    @NotBlank
    @Column(nullable = false)
    private String poziomTrudnosci;

    @NotBlank
    @Column(nullable = false)
    private String rodzajZajec;

    @Min(1)
    @Column(nullable = false)
    private int domyslnyCzasTrwaniaWMinutach;

    @Min(1)
    @Column(nullable = false)
    private int domyslnyLimitOsob;

    @OneToMany(mappedBy = "zajeciaGrupowe")
    private List<SesjaZajec> sesjeZajec = new ArrayList<>();

    protected ZajeciaGrupowe() {
    }

    public ZajeciaGrupowe(String nazwa,
                          String opis,
                          String poziomTrudnosci,
                          String rodzajZajec,
                          int domyslnyCzasTrwaniaWMinutach,
                          int domyslnyLimitOsob) {
        setNazwa(nazwa);
        setOpis(opis);
        setPoziomTrudnosci(poziomTrudnosci);
        setRodzajZajec(rodzajZajec);
        setDomyslnyCzasTrwaniaWMinutach(domyslnyCzasTrwaniaWMinutach);
        setDomyslnyLimitOsob(domyslnyLimitOsob);
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

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        if (nazwa == null || nazwa.isBlank()) {
            throw new IllegalArgumentException("Nazwa zajęć grupowych nie może być pusta.");
        }
        this.nazwa = nazwa;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        if (opis == null || opis.isBlank()) {
            throw new IllegalArgumentException("Opis zajęć grupowych nie może być pusty.");
        }
        this.opis = opis;
    }

    public String getPoziomTrudnosci() {
        return poziomTrudnosci;
    }

    public void setPoziomTrudnosci(String poziomTrudnosci) {
        if (poziomTrudnosci == null || poziomTrudnosci.isBlank()) {
            throw new IllegalArgumentException("Poziom trudności nie może być pusty.");
        }
        this.poziomTrudnosci = poziomTrudnosci;
    }

    public String getRodzajZajec() {
        return rodzajZajec;
    }

    public void setRodzajZajec(String rodzajZajec) {
        if (rodzajZajec == null || rodzajZajec.isBlank()) {
            throw new IllegalArgumentException("Rodzaj zajęć nie może być pusty.");
        }
        this.rodzajZajec = rodzajZajec;
    }

    public int getDomyslnyCzasTrwaniaWMinutach() {
        return domyslnyCzasTrwaniaWMinutach;
    }

    public void setDomyslnyCzasTrwaniaWMinutach(int domyslnyCzasTrwaniaWMinutach) {
        if (domyslnyCzasTrwaniaWMinutach <= 0) {
            throw new IllegalArgumentException("Domyślny czas trwania musi być dodatni.");
        }
        this.domyslnyCzasTrwaniaWMinutach = domyslnyCzasTrwaniaWMinutach;
    }

    public int getDomyslnyLimitOsob() {
        return domyslnyLimitOsob;
    }

    public void setDomyslnyLimitOsob(int domyslnyLimitOsob) {
        if (domyslnyLimitOsob <= 0) {
            throw new IllegalArgumentException("Domyślny limit osób musi być dodatni.");
        }
        this.domyslnyLimitOsob = domyslnyLimitOsob;
    }

    public List<SesjaZajec> getSesjeZajec() {
        return Collections.unmodifiableList(sesjeZajec);
    }

    @Override
    public String toString() {
        return nazwa + " (" + poziomTrudnosci + ")";
    }
}