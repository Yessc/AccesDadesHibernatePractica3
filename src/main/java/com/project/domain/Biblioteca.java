package com.project.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "biblioteques")
public class Biblioteca implements Serializable {

    private static final long serialVersionUID = 1L;
    //biblioteca es Pk que se autogenere id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bibliotecaId;
     @Column(nullable = false)
    private String nom;
    private String ciutat;
    private String adreca;
    private String telefon;
    private String email;

    //mappedBy tiene que apuntar al campo biblioteca dentro del ejemplar
    @OneToMany(mappedBy = "biblioteca", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Exemplar> exemplars = new HashSet<>();

    public Biblioteca() {}

    public Biblioteca(String nom, String ciutat, String adreca, String telefon, String email) {
        this.nom = nom;
        this.ciutat = ciutat;
        this.adreca = adreca;
        this.telefon = telefon;
        this.email = email;
    }

    public Long getBibliotecaId() { return bibliotecaId; }
    public void setBibliotecaId(Long bibliotecaId) { this.bibliotecaId = bibliotecaId; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getCiutat() { return ciutat; }
    public void setCiutat(String ciutat) { this.ciutat = ciutat; }
    public String getAdreca() { return adreca; }
    public void setAdreca(String adreca) { this.adreca = adreca; }
    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<Exemplar> getExemplars() { return exemplars; }
    public void setExemplars(Set<Exemplar> exemplars) { this.exemplars = exemplars; }

    @Override
    public String toString() {
        return "Biblioteca{id=" + bibliotecaId + ", nom='" + nom + "', ciutat='" + ciutat + "'}";
    }
}