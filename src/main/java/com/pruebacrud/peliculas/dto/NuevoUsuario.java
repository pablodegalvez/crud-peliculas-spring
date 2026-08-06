package com.pruebacrud.peliculas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;


public record NuevoUsuario(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 4, max = 20, message = "El nombre de usuario debe tener entre 4 y 20 caracteres")
        String username,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        // Permitimos que al registrarse opcionalmente pasen una lista de strings con sus roles (ej: ["user", "admin"])
        Set<String> roles
) { }
