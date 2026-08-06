package com.pruebacrud.peliculas.controller;

import com.pruebacrud.peliculas.dto.JwtDto;
import com.pruebacrud.peliculas.dto.LoginUsuario;
import com.pruebacrud.peliculas.dto.NuevoUsuario;
import com.pruebacrud.peliculas.model.Rol;
import com.pruebacrud.peliculas.model.RolNombre;
import com.pruebacrud.peliculas.model.Usuario;
import com.pruebacrud.peliculas.repository.RolRepository;
import com.pruebacrud.peliculas.repository.UsuarioRepository;
import com.pruebacrud.peliculas.security.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin // Evita problemas de CORS si conectas un frontend en el futuro
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder; // Es el BCrypt que configuramos en SecurityConfig
    private final JwtProvider jwtProvider;

    // NOTA: Para no meter lógica pesada aquí, idealmente inyectarías un UsuarioService y RolService.
    // Lo hacemos directo para cerrar el flujo limpiamente.
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @PostMapping("/nuevo")
    public ResponseEntity<?> registrar(@Valid @RequestBody NuevoUsuario nuevoUsuario) {
        // 1. Validaciones de unicidad de negocio
        if (usuarioRepository.existsByUsername(nuevoUsuario.username())) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Ese nombre de usuario ya existe"));
        }
        if (usuarioRepository.existsByEmail(nuevoUsuario.email())) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Ese email ya está registrado"));
        }

        // 2. Mapear DTO a Entidad aplicando el Hash irreversible a la contraseña
        Usuario usuario = new Usuario();
        usuario.setUsername(nuevoUsuario.username());
        usuario.setEmail(nuevoUsuario.email());
        usuario.setPassword(passwordEncoder.encode(nuevoUsuario.password())); // ¡HASEADO SEGURO!

        // 3. Asignar Roles de forma segura
        Set<Rol> roles = new HashSet<>();
        // Por defecto, todo usuario registrado es ROLE_USER
        Rol rolUser = rolRepository.findByRolNombre(RolNombre.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: El Rol Base no está inicializado en la BD."));
        roles.add(rolUser);

        // Si el DTO pide explícitamente "admin", le asignamos también ROLE_ADMIN
        if (nuevoUsuario.roles() != null && nuevoUsuario.roles().contains("admin")) {
            Rol rolAdmin = rolRepository.findByRolNombre(RolNombre.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: El Rol Admin no está inicializado en la BD."));
            roles.add(rolAdmin);
        }
        usuario.setRoles(roles);

        // 4. Guardar en PostgreSQL
        usuarioRepository.save(usuario);
        return new ResponseEntity<>(Map.of("mensaje", "Usuario registrado exitosamente"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginUsuario loginUsuario) {
        // 1. Le delegamos al AuthenticationManager la verificación de credenciales.
        // Spring Security irá por detrás a tu UserDetailsService, buscará el usuario,
        // extraerá el hash de la BD y lo comparará matemáticamente con la password plana que viene del DTO.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUsuario.username(), loginUsuario.password())
        );

        // 2. Si las credenciales son válidas, metemos la autenticación en el contexto por si acaso
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generamos el String criptográfico del Token JWT
        String token = jwtProvider.generateToken(authentication);

        // 4. Recuperamos el UserDetails del contexto para rellenar los metadatos del DTO de respuesta
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 5. Devolvemos el DTO con un estado 200 OK
        JwtDto jwtDto = new JwtDto(token, userDetails.getUsername(), userDetails.getAuthorities());
        return ResponseEntity.ok(jwtDto);
    }
}
