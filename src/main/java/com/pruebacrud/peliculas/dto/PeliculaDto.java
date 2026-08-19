package com.pruebacrud.peliculas.dto;

import jakarta.validation.constraints.*;

public record PeliculaDto(
        @NotBlank(message = "El título de la película es un campo obligatorio.")
        @Size(min = 2, max = 100, message = "El título debe tener entre 2 y 100 caracteres.")
        String titulo,

        @Min(value = 1, message = "No se aceptan valores 0 o negativos.")
        Long idDirector,

        @Min(value = 1900, message = "No se aceptan películas anteriores a 1900.")
        @NotNull(message = "La fecha de lanzamiento es obligatoria.")
        Integer fechaLanzamiento,

        @NotNull(message = "La duración en minutos es obligatoria.")
        @Positive(message = "La duración debe ser un número entero mayor que cero.")
        @Max(value = 600, message = "Por motivos de optimización, la duración no puede superar los 600 minutos.")
        Integer duracion
) {
}
