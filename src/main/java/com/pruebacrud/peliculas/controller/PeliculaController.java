package com.pruebacrud.peliculas.controller;


import com.pruebacrud.peliculas.dto.PeliculaDto;
import com.pruebacrud.peliculas.dto.PeliculaResponseDto;
import com.pruebacrud.peliculas.model.Director;
import com.pruebacrud.peliculas.model.Pelicula;
import com.pruebacrud.peliculas.service.PeliculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public List<PeliculaResponseDto> getAll(@RequestParam(required = false) Integer fecha, @RequestParam(required = false) Integer duracionMax) {

        // Si envían el parámetro 'fecha', filtramos por fecha
        if (fecha != null) {
            return peliculaService.listarPeliculaPorFecha(fecha);
        }
        // Si envían el parámetro 'duracionMax', filtramos por duración
        if (duracionMax != null) {
            return peliculaService.listarPeliculaPorDuracion(duracionMax);
        }

        return peliculaService.listarPeliculas();
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
