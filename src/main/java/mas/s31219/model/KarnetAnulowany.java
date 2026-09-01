package mas.s31219.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class KarnetAnulowany extends Karnet {

    @NotNull
    @Column(nullable = false)
    private LocalDate dataAnulowania;

    @NotBlank
    @Column(nullable = false)
    private String powodAnulowania;

    protected KarnetAnulowany() {
    }

    public KarnetAnulowany(LocalDate dataRozpoczecia,
                           LocalDate dataZakonczenia,
                           BigDecimal procentZnizki,
                           PlanCzlonkostwa planCzlonkostwa,
                           CzlonekKlubu czlonekKlubu,
                           LocalDate dataAnulowania,
                           String powodAnulowania) {
        super(dataRozpoczecia, dataZakonczenia, procentZnizki, planCzlonkostwa, czlonekKlubu);
        setDataAnulowania(dataAnulowania);
        setPowodAnulowania(powodAnulowania);
    }

    @Override
    public StatusKarnetu getStatusKarnetu() {
        return StatusKarnetu.ANULOWANY;
    }

    public LocalDate getDataAnulowania() {
        return dataAnulowania;
    }

    public void setDataAnulowania(LocalDate dataAnulowania) {
        if (dataAnulowania == null) {
            throw new IllegalArgumentException("Data anulowania jest wymagana.");
        }
        this.dataAnulowania = dataAnulowania;
    }

    public String getPowodAnulowania() {
        return powodAnulowania;
    }

    public void setPowodAnulowania(String powodAnulowania) {
        if (powodAnulowania == null || powodAnulowania.isBlank()) {
            throw new IllegalArgumentException("Powód anulowania jest wymagany.");
        }
        this.powodAnulowania = powodAnulowania;
    }
}