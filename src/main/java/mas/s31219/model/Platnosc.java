package mas.s31219.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Platnosc {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataPlatnosci;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal kwota;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodaPlatnosci metoda;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPlatnosci status;

    @NotBlank
    @Column(nullable = false)
    private String numerTransakcji;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "karnet_id", nullable = false)
    private Karnet karnet;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "pracownik_klubu_id", nullable = false)
    private PracownikKlubu zarejestrowanaPrzez;

    protected Platnosc() {
    }

    public Platnosc(LocalDateTime dataPlatnosci,
                    BigDecimal kwota,
                    MetodaPlatnosci metoda,
                    StatusPlatnosci status,
                    String numerTransakcji,
                    Karnet karnet,
                    PracownikKlubu zarejestrowanaPrzez) {
        setDataPlatnosci(dataPlatnosci);
        setKwota(kwota);
        setMetoda(metoda);
        setStatus(status);
        setNumerTransakcji(numerTransakcji);
        setKarnet(karnet);
        setZarejestrowanaPrzez(zarejestrowanaPrzez);
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataPlatnosci() {
        return dataPlatnosci;
    }

    public void setDataPlatnosci(LocalDateTime dataPlatnosci) {
        if (dataPlatnosci == null) {
            throw new IllegalArgumentException("Data płatności jest wymagana.");
        }
        this.dataPlatnosci = dataPlatnosci;
    }

    public BigDecimal getKwota() {
        return kwota;
    }

    public void setKwota(BigDecimal kwota) {
        if (kwota == null) {
            throw new IllegalArgumentException("Kwota płatności jest wymagana.");
        }
        if (kwota.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Kwota płatności nie może być ujemna.");
        }
        this.kwota = kwota;
    }

    public MetodaPlatnosci getMetoda() {
        return metoda;
    }

    public void setMetoda(MetodaPlatnosci metoda) {
        if (metoda == null) {
            throw new IllegalArgumentException("Metoda płatności jest wymagana.");
        }
        this.metoda = metoda;
    }

    public StatusPlatnosci getStatus() {
        return status;
    }

    public void setStatus(StatusPlatnosci status) {
        if (status == null) {
            throw new IllegalArgumentException("Status płatności jest wymagany.");
        }
        this.status = status;
    }

    public String getNumerTransakcji() {
        return numerTransakcji;
    }

    public void setNumerTransakcji(String numerTransakcji) {
        if (numerTransakcji == null || numerTransakcji.isBlank()) {
            throw new IllegalArgumentException("Numer transakcji nie może być pusty.");
        }
        this.numerTransakcji = numerTransakcji;
    }

    public Karnet getKarnet() {
        return karnet;
    }

    public void setKarnet(Karnet karnet) {
        if (karnet == null) {
            throw new IllegalArgumentException("Karnet jest wymagany dla płatności.");
        }

        if (this.karnet != null) {
            this.karnet.usunPlatnoscZHistorii(this);
        }

        this.karnet = karnet;
        karnet.dodajPlatnoscDoHistorii(this);
    }

    public PracownikKlubu getZarejestrowanaPrzez() {
        return zarejestrowanaPrzez;
    }

    public void setZarejestrowanaPrzez(PracownikKlubu zarejestrowanaPrzez) {
        if (zarejestrowanaPrzez == null) {
            throw new IllegalArgumentException("Pracownik rejestrujący płatność jest wymagany.");
        }

        if (this.zarejestrowanaPrzez != null) {
            this.zarejestrowanaPrzez.usunZarejestrowanaPlatnosc(this);
        }

        this.zarejestrowanaPrzez = zarejestrowanaPrzez;
        zarejestrowanaPrzez.dodajZarejestrowanaPlatnosc(this);
    }
}