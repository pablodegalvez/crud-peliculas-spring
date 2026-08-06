package com.pruebacrud.peliculas.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginUsuario(
        @NotBlank(message = "El nombre de usuario no puede estar vacío")
        String username,

        @NotBlank(message = "La contraseña no puede estar vacía")
        String password
) {}
