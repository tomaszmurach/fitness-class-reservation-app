package mas.s31219.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class SesjaZajec {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataCzasRozpoczecia;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataCzasZakonczenia;

    @Min(1)
    @Column(nullable = false)
    private int limitOsob;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSesji status;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "zajecia_grupowe_id", nullable = false)
    private ZajeciaGrupowe zajeciaGrupowe;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "sala_treningowa_id", nullable = false)
    private SalaTreningowa salaTreningowa;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "trener_id", nullable = false)
    private Trener trener;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "pracownik_klubu_id", nullable = false)
    private PracownikKlubu zaplanowanePrzez;

    // Asocjacja: jedna sesja zajęć może mieć wiele rezerwacji.
// mappedBy oznacza, że właścicielem relacji jest pole sesjaZajec w klasie Rezerwacja.
    @OneToMany(mappedBy = "sesjaZajec")
    private List<Rezerwacja> rezerwacje = new ArrayList<>();

    protected SesjaZajec() {
    }

    public SesjaZajec(LocalDateTime dataCzasRozpoczecia,
                      LocalDateTime dataCzasZakonczenia,
                      int limitOsob,
                      StatusSesji status,
                      ZajeciaGrupowe zajeciaGrupowe,
                      SalaTreningowa salaTreningowa,
                      Trener trener,
                      PracownikKlubu zaplanowanePrzez) {
        setDataCzasRozpoczecia(dataCzasRozpoczecia);
        setDataCzasZakonczenia(dataCzasZakonczenia);
        setZajeciaGrupowe(zajeciaGrupowe);
        setSalaTreningowa(salaTreningowa);
        setLimitOsob(limitOsob);
        setStatus(status);
        setTrener(trener);
        setZaplanowanePrzez(zaplanowanePrzez);
    }

    // Atrybut pochodny.
// Nie zapisujemy liczby dostępnych miejsc w bazie,
// tylko liczymy ją na podstawie limitu osób i aktywnych rezerwacji.
    public int getDostepneMiejsca() {
        long liczbaAktywnychRezerwacji = rezerwacje.stream()
                .filter(Rezerwacja::czyAktywna)
                .count();

        return limitOsob - (int) liczbaAktywnychRezerwacji;
    }

    public boolean czyMaWolneMiejsca() {
        return getDostepneMiejsca() > 0;
    }

    // Metoda domenowa tworząca rezerwację miejsca na tej konkretnej sesji.
// Chroni spójność obiektu SesjaZajec: sprawdza status, wolne miejsca i duplikat.
    public Rezerwacja zarezerwujMiejsce(CzlonekKlubu czlonekKlubu) {
        if (czlonekKlubu == null) {
            throw new IllegalArgumentException("Członek klubu jest wymagany.");
        }

        // Rezerwacja jest możliwa tylko dla sesji zaplanowanej.
        if (status != StatusSesji.ZAPLANOWANA) {
            throw new IllegalStateException("Rezerwacja jest możliwa tylko dla zaplanowanej sesji zajęć.");
        }

        // Sesja musi mieć wolne miejsca.
        if (!czyMaWolneMiejsca()) {
            throw new IllegalStateException("Brak wolnych miejsc na sesji zajęć.");
        }

        // Dodatkowe zabezpieczenie przed duplikatem w kolekcji rezerwacji tej sesji.
        if (czyCzlonekMaAktywnaRezerwacje(czlonekKlubu)) {
            throw new IllegalStateException("Członek klubu ma już aktywną rezerwację na tę sesję zajęć.");
        }

        // Tworzymy obiekt rezerwacji powiązany z członkiem i tą sesją.
        Rezerwacja rezerwacja = new Rezerwacja(czlonekKlubu, this);

        // Dodajemy rezerwację do kolekcji tej sesji.
        rezerwacje.add(rezerwacja);

        return rezerwacja;
    }

    public void oznaczObecnosc(CzlonekKlubu czlonekKlubu) {
        Rezerwacja rezerwacja = znajdzAktywnaRezerwacje(czlonekKlubu);
        rezerwacja.oznaczObecnosc();
    }

    public void oznaczNieobecnosc(CzlonekKlubu czlonekKlubu) {
        Rezerwacja rezerwacja = znajdzAktywnaRezerwacje(czlonekKlubu);
        rezerwacja.oznaczNieobecnosc();
    }

    public List<Rezerwacja> getRezerwacje() {
        return Collections.unmodifiableList(rezerwacje);
    }

    private boolean czyCzlonekMaAktywnaRezerwacje(CzlonekKlubu czlonekKlubu) {
        return rezerwacje.stream()
                .anyMatch(rezerwacja ->
                        rezerwacja.czyAktywna()
                                && czyTenSamCzlonek(rezerwacja.getCzlonekKlubu(), czlonekKlubu)
                );
    }

    private Rezerwacja znajdzAktywnaRezerwacje(CzlonekKlubu czlonekKlubu) {
        return rezerwacje.stream()
                .filter(rezerwacja ->
                        rezerwacja.czyAktywna()
                                && czyTenSamCzlonek(rezerwacja.getCzlonekKlubu(), czlonekKlubu)
                )
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nie znaleziono aktywnej rezerwacji członka na tę sesję."));
    }

    private boolean czyTenSamCzlonek(CzlonekKlubu pierwszy, CzlonekKlubu drugi) {
        if (pierwszy == null || drugi == null) {
            return false;
        }

        if (pierwszy.getId() != null && drugi.getId() != null) {
            return pierwszy.getId().equals(drugi.getId());
        }

        return pierwszy == drugi;
    }

    public void anulujSesje() {
        if (status == StatusSesji.ANULOWANA) {
            throw new IllegalStateException("Sesja zajęć jest już anulowana.");
        }
        if (status == StatusSesji.ZAKONCZONA) {
            throw new IllegalStateException("Nie można anulować zakończonej sesji zajęć.");
        }
        this.status = StatusSesji.ANULOWANA;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataCzasRozpoczecia() {
        return dataCzasRozpoczecia;
    }

    public void setDataCzasRozpoczecia(LocalDateTime dataCzasRozpoczecia) {
        if (dataCzasRozpoczecia == null) {
            throw new IllegalArgumentException("Data i czas rozpoczęcia sesji są wymagane.");
        }
        this.dataCzasRozpoczecia = dataCzasRozpoczecia;
        sprawdzPoprawnoscTerminu();
    }

    public LocalDateTime getDataCzasZakonczenia() {
        return dataCzasZakonczenia;
    }

    public void setDataCzasZakonczenia(LocalDateTime dataCzasZakonczenia) {
        if (dataCzasZakonczenia == null) {
            throw new IllegalArgumentException("Data i czas zakończenia sesji są wymagane.");
        }
        this.dataCzasZakonczenia = dataCzasZakonczenia;
        sprawdzPoprawnoscTerminu();
    }

    public int getLimitOsob() {
        return limitOsob;
    }

    public void setLimitOsob(int limitOsob) {
        if (limitOsob <= 0) {
            throw new IllegalArgumentException("Limit osób musi być dodatni.");
        }
        if (salaTreningowa != null && limitOsob > salaTreningowa.getPojemnosc()) {
            throw new IllegalArgumentException("Limit osób na sesji nie może przekraczać pojemności sali.");
        }
        this.limitOsob = limitOsob;
    }

    public StatusSesji getStatus() {
        return status;
    }

    public void setStatus(StatusSesji status) {
        if (status == null) {
            throw new IllegalArgumentException("Status sesji jest wymagany.");
        }
        this.status = status;
    }

    public ZajeciaGrupowe getZajeciaGrupowe() {
        return zajeciaGrupowe;
    }

    public void setZajeciaGrupowe(ZajeciaGrupowe zajeciaGrupowe) {
        if (zajeciaGrupowe == null) {
            throw new IllegalArgumentException("Zajęcia grupowe są wymagane.");
        }

        if (this.zajeciaGrupowe != null) {
            this.zajeciaGrupowe.usunSesjeZajec(this);
        }

        this.zajeciaGrupowe = zajeciaGrupowe;
        zajeciaGrupowe.dodajSesjeZajec(this);
    }

    public void setSalaTreningowa(SalaTreningowa salaTreningowa) {
        if (salaTreningowa == null) {
            throw new IllegalArgumentException("Sala treningowa jest wymagana.");
        }
        if (limitOsob > 0 && limitOsob > salaTreningowa.getPojemnosc()) {
            throw new IllegalArgumentException("Limit osób na sesji nie może przekraczać pojemności sali.");
        }

        if (this.salaTreningowa != null) {
            this.salaTreningowa.usunSesjeZajec(this);
        }

        this.salaTreningowa = salaTreningowa;
        salaTreningowa.dodajSesjeZajec(this);
    }

    public Trener getTrener() {
        return trener;
    }

    public void setTrener(Trener trener) {
        if (trener == null) {
            throw new IllegalArgumentException("Trener jest wymagany.");
        }

        if (this.trener != null) {
            this.trener.usunSesjeZajec(this);
        }

        this.trener = trener;
        trener.dodajSesjeZajec(this);
    }

    public PracownikKlubu getZaplanowanePrzez() {
        return zaplanowanePrzez;
    }

    public void setZaplanowanePrzez(PracownikKlubu zaplanowanePrzez) {
        if (zaplanowanePrzez == null) {
            throw new IllegalArgumentException("Pracownik planujący sesję jest wymagany.");
        }

        if (this.zaplanowanePrzez != null) {
            this.zaplanowanePrzez.usunZaplanowanaSesje(this);
        }

        this.zaplanowanePrzez = zaplanowanePrzez;
        zaplanowanePrzez.dodajZaplanowanaSesje(this);
    }

    private void sprawdzPoprawnoscTerminu() {
        if (dataCzasRozpoczecia != null
                && dataCzasZakonczenia != null
                && !dataCzasZakonczenia.isAfter(dataCzasRozpoczecia)) {
            throw new IllegalArgumentException("Data zakończenia sesji musi być późniejsza niż data rozpoczęcia.");
        }
    }

    public @NotNull SalaTreningowa getSalaTreningowa() {
        return salaTreningowa;
    }

    @Override
    public String toString() {
        return zajeciaGrupowe.getNazwa()
                + ", "
                + dataCzasRozpoczecia
                + ", sala: "
                + salaTreningowa.getNumerSali();
    }
}