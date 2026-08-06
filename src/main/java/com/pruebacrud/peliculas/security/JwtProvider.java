package com.pruebacrud.peliculas.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j; // Inyecta el logger automáticamente
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
@Slf4j // Usamos 'log.error()' de forma nativa. Así no tenemos que usar LoggerFactory manual
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration; // Cambiado a 'long' para operaciones de tiempo milisegundos

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // GENERAR EL TOKEN (Sintaxis Moderna JJWT 0.12.x)
    public String generateToken(Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("No se puede generar un token para una autenticación nula o vacía");
        }

        UsuarioAdaptador usuarioAdaptador = (UsuarioAdaptador) authentication.getPrincipal();

        Instant ahora = Instant.now();
        Instant expiracion = ahora.plusMillis(expiration);

        return Jwts.builder()
                .subject(usuarioAdaptador.getUsername())   // Cambiado de setSubject() a subject()
                .issuedAt(Date.from(ahora))                // Cambiado de setIssuedAt() a issuedAt()
                .expiration(Date.from(expiracion))         // Cambiado de setExpiration() a expiration()
                .signWith(key)                             // En 0.12.x se pasa solo la llave; autodetecta HS256
                .compact();
    }

    // EXTRAER EL USERNAME
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // VALIDAR EL TOKEN
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Token mal formado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token no soportado: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token expirado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Token vacío: {}", e.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("Fallo en la firma digital: {}", e.getMessage());
        }
        return false;
    }
}
