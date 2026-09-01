package mas.s31219.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class PlanCzlonkostwa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String nazwa;

    @Min(1)
    @Column(nullable = false)
    private int czasTrwaniaWDniach;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cena;

    @Column(nullable = false)
    private boolean pozwalaNaZajeciaGrupowe;

    @OneToMany(mappedBy = "planCzlonkostwa")
    private List<Karnet> karnety = new ArrayList<>();

    protected PlanCzlonkostwa() {
    }

    public PlanCzlonkostwa(String nazwa,
                           int czasTrwaniaWDniach,
                           BigDecimal cena,
                           boolean pozwalaNaZajeciaGrupowe) {
        setNazwa(nazwa);
        setCzasTrwaniaWDniach(czasTrwaniaWDniach);
        setCena(cena);
        setPozwalaNaZajeciaGrupowe(pozwalaNaZajeciaGrupowe);
    }

    public LocalDate obliczDateZakonczenia(LocalDate dataRozpoczecia) {
        if (dataRozpoczecia == null) {
            throw new IllegalArgumentException("Data rozpoczęcia jest wymagana.");
        }
        return dataRozpoczecia.plusDays(czasTrwaniaWDniach);
    }

    void dodajKarnetDoHistorii(Karnet karnet) {
        if (karnet == null) {
            throw new IllegalArgumentException("Karnet jest wymagany.");
        }

        if (!karnety.contains(karnet)) {
            karnety.add(karnet);
        }
    }

    void usunKarnetZHistorii(Karnet karnet) {
        karnety.remove(karnet);
    }

    public Long getId() {
        return id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        if (nazwa == null || nazwa.isBlank()) {
            throw new IllegalArgumentException("Nazwa planu członkostwa nie może być pusta.");
        }
        this.nazwa = nazwa;
    }

    public int getCzasTrwaniaWDniach() {
        return czasTrwaniaWDniach;
    }

    public void setCzasTrwaniaWDniach(int czasTrwaniaWDniach) {
        if (czasTrwaniaWDniach <= 0) {
            throw new IllegalArgumentException("Czas trwania planu musi być dodatni.");
        }
        this.czasTrwaniaWDniach = czasTrwaniaWDniach;
    }

    public BigDecimal getCena() {
        return cena;
    }

    public void setCena(BigDecimal cena) {
        if (cena == null) {
            throw new IllegalArgumentException("Cena planu jest wymagana.");
        }
        if (cena.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cena planu nie może być ujemna.");
        }
        this.cena = cena;
    }

    public boolean isPozwalaNaZajeciaGrupowe() {
        return pozwalaNaZajeciaGrupowe;
    }

    public void setPozwalaNaZajeciaGrupowe(boolean pozwalaNaZajeciaGrupowe) {
        this.pozwalaNaZajeciaGrupowe = pozwalaNaZajeciaGrupowe;
    }

    public List<Karnet> getKarnety() {
        return Collections.unmodifiableList(karnety);
    }

    @Override
    public String toString() {
        return nazwa;
    }
}