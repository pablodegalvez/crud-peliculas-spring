package com.pruebacrud.peliculas.controller;


import com.pruebacrud.peliculas.model.Director;
import com.pruebacrud.peliculas.service.DirectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/directores")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public List<Director> getAll() {
        return directorService.listarDirector();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Director> getDirector (@PathVariable Long id) {
        return ResponseEntity.ok(directorService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Director> addDirector (@RequestBody Director nuevoDirector) {
        return ResponseEntity.status(HttpStatus.CREATED).body(directorService.guardarDirector(nuevoDirector));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Director> updateDirector (@PathVariable Long id, @RequestBody Director directorActualizado) {
        return ResponseEntity.ok(directorService.actualizarDirector(id, directorActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Director> deleteDirector (@PathVariable Long id) {
        return ResponseEntity.ok(directorService.eliminarDirector(id));
    }


}
