package com.pruebacrud.peliculas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "directores")
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "nacionalidad", nullable = false, length = 100)
    private String nacionalidad;

    @Column(name = "activo", nullable = false, columnDefinition = "boolean default true")
    private boolean activo = true;

    @OneToMany(mappedBy = "director", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("director")
    private List<Pelicula> peliculaList;
}
