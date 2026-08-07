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

                // 2. NUEVO: Permitir conexiones desde cualquier origen exterior (CORS)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(java.util.List.of("*")); // Permite cualquier origen (incluyendo file://)
                    corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                    return corsConfiguration;
                }))

                // 2. Acoplamos nuestro manejador de excepciones personalizado para los 401 Unauthorized
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtEntryPoint))

                // 3. Política Stateless: API pura sin estado
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Configurar reglas granulares para Películas y Directores
                .authorizeHttpRequests(auth -> auth
                        // Permitimos acceso público total al controlador de autenticación (Login y Registro)
                        .requestMatchers("/api/auth/**").permitAll()

                        // REGLA CRUD: Permitir GETs (Lectura) públicos a películas y directores
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/**", "/api/directores/**").permitAll()

                        // REGLA CRUD: Exigir ROLE_ADMIN para modificar, crear o eliminar datos (POST, PUT, DELETE)
                        .requestMatchers(HttpMethod.POST, "/api/peliculas/**", "/api/directores/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/peliculas/**", "/api/directores/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/peliculas/**", "/api/directores/**").hasRole("ADMIN")

                        // Cualquier otra ruta no especificada requerirá estar mínimamente autenticado (ROLE_USER o ROLE_ADMIN)
                        .anyRequest().authenticated()
                );

        // El filtro JWT va antes del filtro de login por usuario/contraseña tradicional
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
