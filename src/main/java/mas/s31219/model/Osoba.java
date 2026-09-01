package mas.s31219.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Osoba {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String imie;

    @NotBlank
    @Column(nullable = false)
    private String nazwisko;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataUrodzenia;

    @NotBlank
    @Column(nullable = false)
    private String numerTelefonu;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    protected Osoba() {
    }

    protected Osoba(String imie, String nazwisko, LocalDate dataUrodzenia, String numerTelefonu, String email) {
        setImie(imie);
        setNazwisko(nazwisko);
        setDataUrodzenia(dataUrodzenia);
        setNumerTelefonu(numerTelefonu);
        setEmail(email);
    }

    public int obliczWiek() {
        return Period.between(dataUrodzenia, LocalDate.now()).getYears();
    }

    public String pobierzDaneKontaktowe() {
        return imie + " " + nazwisko + ", tel.: " + numerTelefonu + ", email: " + email;
    }

    public Long getId() {
        return id;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        if (imie == null || imie.isBlank()) {
            throw new IllegalArgumentException("Imię nie może być puste.");
        }
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        if (nazwisko == null || nazwisko.isBlank()) {
            throw new IllegalArgumentException("Nazwisko nie może być puste.");
        }
        this.nazwisko = nazwisko;
    }

    public LocalDate getDataUrodzenia() {
        return dataUrodzenia;
    }

    public void setDataUrodzenia(LocalDate dataUrodzenia) {
        if (dataUrodzenia == null) {
            throw new IllegalArgumentException("Data urodzenia jest wymagana.");
        }
        if (dataUrodzenia.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data urodzenia nie może być z przyszłości.");
        }
        this.dataUrodzenia = dataUrodzenia;
    }

    public String getNumerTelefonu() {
        return numerTelefonu;
    }

    public void setNumerTelefonu(String numerTelefonu) {
        if (numerTelefonu == null || numerTelefonu.isBlank()) {
            throw new IllegalArgumentException("Numer telefonu nie może być pusty.");
        }
        this.numerTelefonu = numerTelefonu;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email nie może być pusty.");
        }
        this.email = email;
    }

    @Override
    public String toString() {
        return imie + " " + nazwisko;
    }
}

