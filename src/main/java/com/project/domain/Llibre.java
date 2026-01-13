package com.project.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

//la tabla sera llibres
@Entity
@Table(name = "llibres")
public class Llibre implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long llibreId;

    private String isbn;
    private String titol;
    private String editorial;
    private Integer anyPublicacio;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "llibre_autor",
        joinColumns = @JoinColumn(name = "llibre_id"),
        inverseJoinColumns = @JoinColumn(name = "autor_id")
    )

    // TODO 3: Relació ManyToMany amb Autor.
    // PISTA: Aquesta entitat és la "propietària" de la relació. 
    // Cal definir aquí el @JoinTable explícitament.
    // PISTA EXTRA: Fes servir fetch = FetchType.LAZY per eficiència.
    private Set<Autor> autors = new HashSet<>();

    // TODO 4: Relació OneToMany amb Exemplar.
    // PISTA: mappedBy = "llibre"

    @OneToMany(mappedBy = "llibre", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Exemplar> exemplars = new HashSet<>();

    public Llibre() {}

    public Llibre(String isbn, String titol, String editorial, Integer anyPublicacio) {
        this.isbn = isbn;
        this.titol = titol;
        this.editorial = editorial;
        this.anyPublicacio = anyPublicacio;
    }

    public Long getLlibreId() { return llibreId; }
    public void setLlibreId(Long llibreId) { this.llibreId = llibreId; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitol() { return titol; }
    public void setTitol(String titol) { this.titol = titol; }
    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    public Integer getAnyPublicacio() { return anyPublicacio; }
    public void setAnyPublicacio(Integer anyPublicacio) { this.anyPublicacio = anyPublicacio; }
    public Set<Autor> getAutors() { return autors; }
    public void setAutors(Set<Autor> autors) { this.autors = autors; }
    public Set<Exemplar> getExemplars() { return exemplars; }
    public void setExemplars(Set<Exemplar> exemplars) { this.exemplars = exemplars; }

    @Override
    public String toString() {
        // PISTA: No imprimim les llistes (autors/exemplars) per evitar bucles infinits
        return "Llibre{id=" + llibreId + ", isbn='" + isbn + "', titol='" + titol + "'}";
    }
}