package mas.s31219.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Karnet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataRozpoczecia;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataZakonczenia;

    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal procentZnizki;

    @Column(nullable = false)
    private boolean aktualny = true;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_czlonkostwa_id", nullable = false)
    private PlanCzlonkostwa planCzlonkostwa;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "czlonek_klubu_id", nullable = false)
    private CzlonekKlubu czlonekKlubu;

    @OneToMany(mappedBy = "karnet")
    private List<Platnosc> platnosci = new ArrayList<>();

    protected Karnet() {
    }

    protected Karnet(LocalDate dataRozpoczecia,
                     LocalDate dataZakonczenia,
                     BigDecimal procentZnizki,
                     PlanCzlonkostwa planCzlonkostwa,
                     CzlonekKlubu czlonekKlubu) {
        setDataRozpoczecia(dataRozpoczecia);
        setDataZakonczenia(dataZakonczenia);
        setProcentZnizki(procentZnizki);
        setPlanCzlonkostwa(planCzlonkostwa);
        setCzlonekKlubu(czlonekKlubu);
    }

    public abstract StatusKarnetu getStatusKarnetu();

    void oznaczJakoNieaktualny() {
        this.aktualny = false;
    }

    void oznaczJakoAktualny() {
        this.aktualny = true;
    }

    void dodajPlatnoscDoHistorii(Platnosc platnosc) {
        if (platnosc == null) {
            throw new IllegalArgumentException("Płatność jest wymagana.");
        }

        if (!platnosci.contains(platnosc)) {
            platnosci.add(platnosc);
        }
    }

    void usunPlatnoscZHistorii(Platnosc platnosc) {
        platnosci.remove(platnosc);
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDataRozpoczecia() {
        return dataRozpoczecia;
    }

    public void setDataRozpoczecia(LocalDate dataRozpoczecia) {
        if (dataRozpoczecia == null) {
            throw new IllegalArgumentException("Data rozpoczęcia karnetu jest wymagana.");
        }
        this.dataRozpoczecia = dataRozpoczecia;
    }

    public LocalDate getDataZakonczenia() {
        return dataZakonczenia;
    }

    public void setDataZakonczenia(LocalDate dataZakonczenia) {
        if (dataZakonczenia == null) {
            throw new IllegalArgumentException("Data zakończenia karnetu jest wymagana.");
        }
        this.dataZakonczenia = dataZakonczenia;
    }

    public BigDecimal getProcentZnizki() {
        return procentZnizki;
    }

    public void setProcentZnizki(BigDecimal procentZnizki) {
        if (procentZnizki == null) {
            throw new IllegalArgumentException("Procent zniżki jest wymagany.");
        }
        if (procentZnizki.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Procent zniżki nie może być ujemny.");
        }
        if (procentZnizki.compareTo(new BigDecimal("100.00")) > 0) {
            throw new IllegalArgumentException("Procent zniżki nie może przekraczać 100.");
        }
        this.procentZnizki = procentZnizki;
    }

    public boolean isAktualny() {
        return aktualny;
    }

    public PlanCzlonkostwa getPlanCzlonkostwa() {
        return planCzlonkostwa;
    }

    public void setPlanCzlonkostwa(PlanCzlonkostwa planCzlonkostwa) {
        if (planCzlonkostwa == null) {
            throw new IllegalArgumentException("Plan członkostwa jest wymagany.");
        }

        if (this.planCzlonkostwa != null) {
            this.planCzlonkostwa.usunKarnetZHistorii(this);
        }

        this.planCzlonkostwa = planCzlonkostwa;
        planCzlonkostwa.dodajKarnetDoHistorii(this);
    }

    public CzlonekKlubu getCzlonekKlubu() {
        return czlonekKlubu;
    }

    public void setCzlonekKlubu(CzlonekKlubu czlonekKlubu) {
        if (czlonekKlubu == null) {
            throw new IllegalArgumentException("Członek klubu jest wymagany.");
        }

        if (this.czlonekKlubu != null) {
            this.czlonekKlubu.usunKarnetZHistorii(this);
        }

        this.czlonekKlubu = czlonekKlubu;
        czlonekKlubu.dodajKarnetDoHistorii(this);
    }

    public List<Platnosc> getPlatnosci() {
        return Collections.unmodifiableList(platnosci);
    }
}