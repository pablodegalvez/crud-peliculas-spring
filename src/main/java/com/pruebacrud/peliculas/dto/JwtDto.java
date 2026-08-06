package com.pruebacrud.peliculas.dto;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public record JwtDto(
        String token,
        String type, // Normalmente "Bearer" para que el frontend sepa el estándar
        String username,
        Collection<? extends GrantedAuthority> authorities // Para que el frontend sepa qué vistas ocultar/mostrar
) {
    // Constructor compacto para asignar "Bearer" por defecto de forma elegante
    public JwtDto(String token, String username, Collection<? extends GrantedAuthority> authorities) {
        this(token, "Bearer", username, authorities);
    }
}
