package com.pruebacrud.peliculas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils; // Importante para la validación segura
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;



@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Extraer el token crudo de la cabecera HTTP
            String token = getToken(request);

            // Si el token existe, no son espacios en blanco y es válido criptográficamente, procedemos a identificar al usuario
            if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {

                // Extraemos el username que guardamos dentro del token
                String username = jwtProvider.getUsernameFromToken(token);

                // Cargamos el UserDetails (nuestro UsuarioPrincipal) desde la base de datos PostgreSQL
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Creamos el objeto de autenticación definitivo con los datos y sus roles (authorities)
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Añadimos detalles extra de la petición web (como la IP o la sesión) al objeto de autenticación
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Metemos al usuario autenticado dentro del Contexto de Seguridad de Spring.
                // A partir de esta línea, para Spring Boot, este usuario está oficialmente "Logueado" en este hilo.
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            // Modificado para usar la optimización de llaves {} de SLF4J
            log.error("No se pudo establecer la autenticación de usuario en el filtro: {}", e.getMessage());
        }

        // OBLIGATORIO: Dejar que la petición continúe su viaje hacia el siguiente filtro o el Controller
        filterChain.doFilter(request, response);
    }

    // Método utilitario privado para diseccionar la cabecera Authorization
    private String getToken(HttpServletRequest request) {
        //El token normalmente viaja dentro de la cabecera http llamada Authorization
        String header = request.getHeader("Authorization");

        // El estándar internacional exige que los tokens viajen con el prefijo "Bearer " (ej: Bearer eyJhbGci...)
        //El token se suele transmitir empezando con Bearer + el token
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7); // Quitamos el prefijo para quedarnos solo con el string del JWT
        }
        return null;
    }

}
