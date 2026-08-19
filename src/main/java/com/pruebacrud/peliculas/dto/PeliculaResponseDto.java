package com.pruebacrud.peliculas.dto;

public record PeliculaResponseDto(
        Long id,
        String titulo,
        Integer fechaLanzamiento,
        Integer duracion,
        DirectorDto director // <-- Esto hace match con ${pelicula.director.nombre}
) {
}
