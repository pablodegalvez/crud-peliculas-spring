package com.pruebacrud.peliculas.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Nos permite usar anotaciones como @PreAuthorize en los controladores si quisiéramos
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final JwtEntryPoint jwtEntryPoint; // Inyectamos el manejador de errores 401

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Deshabilitar CSRF (No usamos cookies, usamos JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Acoplamos nuestro manejador de excepciones personalizado para los 401 Unauthorized
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtEntryPoint))

                // 3. Política Stateless: API pura sin estado
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Configurar reglas granulares para Películas y Directores
                .authorizeHttpRequests(auth -> auth
                        // Permitimos acceso público total al controlador de autenticación (Login y Registro)
                        .requestMatchers("/auth/**").permitAll()

                        // REGLA CRUD: Permitir GETs (Lectura) públicos a películas y directores
                        .requestMatchers(HttpMethod.GET, "/peliculas/**", "/directores/**").permitAll()

                        // REGLA CRUD: Exigir ROLE_ADMIN para modificar, crear o eliminar datos (POST, PUT, DELETE)
                        .requestMatchers(HttpMethod.POST, "/peliculas/**", "/directores/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/peliculas/**", "/directores/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/peliculas/**", "/directores/**").hasRole("ADMIN")

                        // Cualquier otra ruta no especificada requerirá estar mínimamente autenticado (ROLE_USER o ROLE_ADMIN)
                        .anyRequest().authenticated()
                );

        // El filtro JWT va antes del filtro de login por usuario/contraseña tradicional
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
