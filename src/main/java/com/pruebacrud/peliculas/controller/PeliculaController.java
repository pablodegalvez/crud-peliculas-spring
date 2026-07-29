package com.pruebacrud.peliculas.controller;


import com.pruebacrud.peliculas.model.Pelicula;
import com.pruebacrud.peliculas.service.PeliculaService;
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
    public List<Pelicula> getAll(@RequestParam(required = false) Integer fecha, @RequestParam(required = false) Integer duracionMax) {

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
    public ResponseEntity<Pelicula> getPelicula(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Pelicula> addPelicula (@RequestBody Pelicula nuevaPelicula) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peliculaService.guardarPelicula(nuevaPelicula));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pelicula> updatePelicula (@PathVariable Long id, @RequestBody Pelicula peliculaActualizada) {
        return ResponseEntity.ok(peliculaService.actualizarPelicula(id, peliculaActualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Pelicula> deletePelicula (@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.eliminarPelicula(id));
    }

}
