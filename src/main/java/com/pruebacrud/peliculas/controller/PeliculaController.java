package com.pruebacrud.peliculas.controller;


import com.pruebacrud.peliculas.dto.PeliculaDto;
import com.pruebacrud.peliculas.dto.PeliculaResponseDto;
import com.pruebacrud.peliculas.model.Director;
import com.pruebacrud.peliculas.model.Pelicula;
import com.pruebacrud.peliculas.service.PeliculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peliculas")
@RequiredArgsConstructor
public class PeliculaController {

    private final PeliculaService peliculaService;

    @GetMapping
    public ResponseEntity<Page<PeliculaResponseDto>> getAll(
            @RequestParam(required = false) Integer fecha,
            @RequestParam(required = false) Integer duracionMax,
            // @PageableDefault configura los valores por defecto si no vienen en la URL
            @PageableDefault(page = 0, size = 10, sort = "titulo") Pageable pageable
    ) {

        // Si envían el parámetro 'fecha', filtramos por fecha
        if (fecha != null) {
            return ResponseEntity.ok(peliculaService.listarPeliculaPorFecha(fecha, pageable));
        }
        // Si envían el parámetro 'duracionMax', filtramos por duración
        if (duracionMax != null) {
            return ResponseEntity.ok(peliculaService.listarPeliculaPorDuracion(duracionMax, pageable));
        }

        return ResponseEntity.ok(peliculaService.listarPeliculas(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeliculaResponseDto> getPelicula(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.obtenerPorIdDto(id));
    }

    @PostMapping
    public ResponseEntity<PeliculaResponseDto> addPelicula (@Valid @RequestBody PeliculaDto nuevaPeliculaDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(peliculaService.guardarPelicula(nuevaPeliculaDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PeliculaResponseDto> updatePelicula (@PathVariable Long id, @RequestBody PeliculaDto peliculaActualizada) {
        return ResponseEntity.ok(peliculaService.actualizarPelicula(id, peliculaActualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PeliculaResponseDto> deletePelicula (@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.eliminarPelicula(id));
    }

}
