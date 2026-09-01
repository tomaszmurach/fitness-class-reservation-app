package mas.s31219.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class Rezerwacja {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataRezerwacji;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusRezerwacji status;

    // Rezerwacja należy do jednego członka klubu.
// Wiele rezerwacji może dotyczyć tego samego członka.
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "czlonek_klubu_id", nullable = false)
    private CzlonekKlubu czlonekKlubu;

    // Rezerwacja należy do jednej sesji zajęć.
// Wiele rezerwacji może dotyczyć tej samej sesji.
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "sesja_zajec_id", nullable = false)
    private SesjaZajec sesjaZajec;

    protected Rezerwacja() {
    }

    // Konstruktor pakietowy - rezerwacja nie jest tworzona bezpośrednio z GUI.
// Powstaje przez metodę SesjaZajec.zarezerwujMiejsce(...).
    Rezerwacja(CzlonekKlubu czlonekKlubu, SesjaZajec sesjaZajec) {
        // Ustawienie członka i dopisanie rezerwacji do historii członka.
        setCzlonekKlubu(czlonekKlubu);

        // Ustawienie sesji, której dotyczy rezerwacja.
        setSesjaZajec(sesjaZajec);

        // Data utworzenia rezerwacji.
        this.dataRezerwacji = LocalDateTime.now();

        // Nowa rezerwacja zawsze zaczyna jako aktywna.
        this.status = StatusRezerwacji.AKTYWNA;
    }

    public void anuluj() {
        if (status != StatusRezerwacji.AKTYWNA) {
            throw new IllegalStateException("Można anulować tylko aktywną rezerwację.");
        }
        this.status = StatusRezerwacji.ANULOWANA;
    }

    public void oznaczObecnosc() {
        if (status != StatusRezerwacji.AKTYWNA) {
            throw new IllegalStateException("Obecność można oznaczyć tylko dla aktywnej rezerwacji.");
        }
        this.status = StatusRezerwacji.OBECNOSC_POTWIERDZONA;
    }

    public void oznaczNieobecnosc() {
        if (status != StatusRezerwacji.AKTYWNA) {
            throw new IllegalStateException("Nieobecność można oznaczyć tylko dla aktywnej rezerwacji.");
        }
        this.status = StatusRezerwacji.NIEOBECNOSC;
    }

    public boolean czyAktywna() {
        return status == StatusRezerwacji.AKTYWNA;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataRezerwacji() {
        return dataRezerwacji;
    }

    public StatusRezerwacji getStatus() {
        return status;
    }

    public CzlonekKlubu getCzlonekKlubu() {
        return czlonekKlubu;
    }

    void setCzlonekKlubu(CzlonekKlubu czlonekKlubu) {
        if (czlonekKlubu == null) {
            throw new IllegalArgumentException("Członek klubu jest wymagany dla rezerwacji.");
        }

        if (this.czlonekKlubu != null) {
            this.czlonekKlubu.usunRezerwacjeZHistorii(this);
        }

        this.czlonekKlubu = czlonekKlubu;
        czlonekKlubu.dodajRezerwacjeDoHistorii(this);
    }

    public SesjaZajec getSesjaZajec() {
        return sesjaZajec;
    }

    void setSesjaZajec(SesjaZajec sesjaZajec) {
        if (sesjaZajec == null) {
            throw new IllegalArgumentException("Sesja zajęć jest wymagana dla rezerwacji.");
        }
        this.sesjaZajec = sesjaZajec;
    }

    @Override
    public String toString() {
        return czlonekKlubu + " - " + sesjaZajec + " - " + status;
    }
}