package mas.s31219.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class KarnetAktywny extends Karnet {

    @NotNull
    @Column(nullable = false)
    private LocalDate dataAktywacji;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataNastepnejPlatnosci;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataOstatniejPlatnosci;

    protected KarnetAktywny() {
    }

    public KarnetAktywny(LocalDate dataRozpoczecia,
                         LocalDate dataZakonczenia,
                         BigDecimal procentZnizki,
                         PlanCzlonkostwa planCzlonkostwa,
                         CzlonekKlubu czlonekKlubu,
                         LocalDate dataAktywacji,
                         LocalDate dataNastepnejPlatnosci,
                         LocalDate dataOstatniejPlatnosci) {
        super(dataRozpoczecia, dataZakonczenia, procentZnizki, planCzlonkostwa, czlonekKlubu);
        setDataAktywacji(dataAktywacji);
        setDataNastepnejPlatnosci(dataNastepnejPlatnosci);
        setDataOstatniejPlatnosci(dataOstatniejPlatnosci);
    }

    public KarnetZawieszony zawies(String powod) {
        return new KarnetZawieszony(
                getDataRozpoczecia(),
                getDataZakonczenia(),
                getProcentZnizki(),
                getPlanCzlonkostwa(),
                getCzlonekKlubu(),
                LocalDate.now(),
                null,
                powod
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
        return StatusKarnetu.AKTYWNY;
    }

    public LocalDate getDataAktywacji() {
        return dataAktywacji;
    }

    public void setDataAktywacji(LocalDate dataAktywacji) {
        if (dataAktywacji == null) {
            throw new IllegalArgumentException("Data aktywacji jest wymagana.");
        }
        this.dataAktywacji = dataAktywacji;
    }

    public LocalDate getDataNastepnejPlatnosci() {
        return dataNastepnejPlatnosci;
    }

    public void setDataNastepnejPlatnosci(LocalDate dataNastepnejPlatnosci) {
        if (dataNastepnejPlatnosci == null) {
            throw new IllegalArgumentException("Data następnej płatności jest wymagana.");
        }
        this.dataNastepnejPlatnosci = dataNastepnejPlatnosci;
    }

    public LocalDate getDataOstatniejPlatnosci() {
        return dataOstatniejPlatnosci;
    }

    public void setDataOstatniejPlatnosci(LocalDate dataOstatniejPlatnosci) {
        if (dataOstatniejPlatnosci == null) {
            throw new IllegalArgumentException("Data ostatniej płatności jest wymagana.");
        }
        this.dataOstatniejPlatnosci = dataOstatniejPlatnosci;
    }
}