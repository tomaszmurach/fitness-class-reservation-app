package mas.s31219.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class KarnetZawieszony extends Karnet {

    @NotNull
    @Column(nullable = false)
    private LocalDate dataRozpoczeciaZawieszenia;

    @Column
    private LocalDate dataZakonczeniaZawieszenia;

    @NotBlank
    @Column(nullable = false)
    private String powodZawieszenia;

    protected KarnetZawieszony() {
    }

    public KarnetZawieszony(LocalDate dataRozpoczecia,
                            LocalDate dataZakonczenia,
                            BigDecimal procentZnizki,
                            PlanCzlonkostwa planCzlonkostwa,
                            CzlonekKlubu czlonekKlubu,
                            LocalDate dataRozpoczeciaZawieszenia,
                            LocalDate dataZakonczeniaZawieszenia,
                            String powodZawieszenia) {
        super(dataRozpoczecia, dataZakonczenia, procentZnizki, planCzlonkostwa, czlonekKlubu);
        setDataRozpoczeciaZawieszenia(dataRozpoczeciaZawieszenia);
        setDataZakonczeniaZawieszenia(dataZakonczeniaZawieszenia);
        setPowodZawieszenia(powodZawieszenia);
    }

    public KarnetAktywny wznow() {
        return new KarnetAktywny(
                getDataRozpoczecia(),
                getDataZakonczenia(),
                getProcentZnizki(),
                getPlanCzlonkostwa(),
                getCzlonekKlubu(),
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                null
        );
    }

    public KarnetAnulowany anuluj(String powod) {
        return new KarnetAnulowany(
                getDataRozpoczecia(),
                getDataZakonczenia(),
                getProcentZnizki(),
                getPlanCzlonkostwa(),
                getCzlonekKlubu(),
                LocalDate.now(),
                powod
        );
    }

    @Override
    public StatusKarnetu getStatusKarnetu() {
        return StatusKarnetu.ZAWIESZONY;
    }

    public LocalDate getDataRozpoczeciaZawieszenia() {
        return dataRozpoczeciaZawieszenia;
    }

    public void setDataRozpoczeciaZawieszenia(LocalDate dataRozpoczeciaZawieszenia) {
        if (dataRozpoczeciaZawieszenia == null) {
            throw new IllegalArgumentException("Data rozpoczęcia zawieszenia jest wymagana.");
        }
        this.dataRozpoczeciaZawieszenia = dataRozpoczeciaZawieszenia;
    }

    public LocalDate getDataZakonczeniaZawieszenia() {
        return dataZakonczeniaZawieszenia;
    }

    public void setDataZakonczeniaZawieszenia(LocalDate dataZakonczeniaZawieszenia) {
        this.dataZakonczeniaZawieszenia = dataZakonczeniaZawieszenia;
    }

    public String getPowodZawieszenia() {
        return powodZawieszenia;
    }

    public void setPowodZawieszenia(String powodZawieszenia) {
        if (powodZawieszenia == null || powodZawieszenia.isBlank()) {
            throw new IllegalArgumentException("Powód zawieszenia jest wymagany.");
        }
        this.powodZawieszenia = powodZawieszenia;
    }
}