package com.pruebacrud.peliculas.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.error("Fallo en la autenticación: {}", authException.getMessage());

        // Configuramos la respuesta HTTP para que devuelva un JSON estructurado
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 401
        response.getWriter().write("{\"status\": 401, \"error\": \"No autorizado\", \"mensaje\": \"" + authException.getMessage() + "\"}");

        /*
        Otra forma de hacerlo sería con ObjectMapper
        Es necesario inyectar ObjectMapper con @RequiredArgsConstructor y con private final ObjectMapper objectMapper;

        // Estructura segura usando un mapa que Jackson convertirá a JSON perfectamente
        Map<String, Object> errorDetalles = new HashMap<>();
        errorDetalles.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorDetalles.put("error", "No autorizado");
        errorDetalles.put("mensaje", authException.getMessage());

        objectMapper.writeValue(response.getWriter(), errorDetalles);
         */
    }
}
