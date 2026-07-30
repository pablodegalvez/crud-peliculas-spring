package com.pruebacrud.peliculas.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "peliculas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false, length = 100, unique = true)
    private String titulo;

    @Column(name = "fecha_de_lanzamiento", nullable = true)
    private Integer fechaLanzamiento;

    @Column(name = "duracion", nullable = true, unique = false)
    private Integer duracion;

    @Column(name = "activo", nullable = false, columnDefinition = "boolean default true")
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id", nullable = true)
    @JsonIgnoreProperties("peliculaList")
    private Director director;


}
