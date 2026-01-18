package com.project.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exemplars")
public class Exemplar implements Serializable {

    private static final long serialVersionUID = 1L;
    
    // TODO 2: @Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exemplarId;

    // TODO 3: @Column amb unique = true
    @Column(unique = true, nullable = false)
    private String codiBarres;

    private boolean disponible;

    // TODO 4: Relació ManyToOne amb Llibre
    // @JoinColumn(name = "llibre_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "llibre_id", nullable = false)
    private Llibre llibre;

    // TODO 5: Relació ManyToOne amb Biblioteca
    // @JoinColumn(name = "biblioteca_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "biblioteca_id", nullable = false)
    private Biblioteca biblioteca;

    // TODO 6: Relació OneToMany amb Prestec (historial)
     @OneToMany(mappedBy = "exemplar", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Prestec> historialPrestecs = new HashSet<>();

    public Exemplar() {}

    public Exemplar(String codiBarres, Llibre llibre, Biblioteca biblioteca) {
        this.codiBarres = codiBarres;
        this.llibre = llibre;
        this.biblioteca = biblioteca;
        this.disponible = true; // Per defecte disponible
    }

    public Long getExemplarId() { return exemplarId; }
    public void setExemplarId(Long exemplarId) { this.exemplarId = exemplarId; }
    public String getCodiBarres() { return codiBarres; }
    public void setCodiBarres(String codiBarres) { this.codiBarres = codiBarres; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public Llibre getLlibre() { return llibre; }
    public void setLlibre(Llibre llibre) { this.llibre = llibre; }
    public Biblioteca getBiblioteca() { return biblioteca; }
    public void setBiblioteca(Biblioteca biblioteca) { this.biblioteca = biblioteca; }
    public Set<Prestec> getHistorialPrestecs() { return historialPrestecs; }
    public void setHistorialPrestecs(Set<Prestec> historialPrestecs) { this.historialPrestecs = historialPrestecs; }

    @Override
    public String toString() {
        // Accés segur al títol del llibre
        String titol = (llibre != null) ? llibre.getTitol() : "Desconegut";
        return "Exemplar{id=" + exemplarId + ", codi='" + codiBarres + "', llibre='" + titol + "', disponible=" + disponible + "}";
    }
}