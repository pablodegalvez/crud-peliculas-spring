package com.pruebacrud.peliculas.service;


import com.pruebacrud.peliculas.exception.RecursoNoEncontradoException;
import com.pruebacrud.peliculas.model.Director;
import com.pruebacrud.peliculas.repository.DirectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;

    public List<Director> listarDirector () {
        return directorRepository.findAllDirectorOptimizado();
    }

    public Director obtenerPorId (Long id) {
        Director director = directorRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("El director con ID " + id + " no existe."));

        if(!director.getActivo()) {
            throw new RecursoNoEncontradoException("El director con ID " + id + " ha sido dada de baja.");
        }

        return director;
    }

    public Director guardarDirector (Director nuevoDirector) {
        nuevoDirector.setId(null);
        nuevoDirector.setActivo(true);
        return directorRepository.save(nuevoDirector);
    }

    public Director actualizarDirector (Long id, Director directorModificado) {
        Director directorAntiguo = obtenerPorId(id);

        directorAntiguo.setNombre(directorModificado.getNombre());
        directorAntiguo.setNacionalidad(directorModificado.getNacionalidad());

        return directorRepository.save(directorAntiguo);
    }

    public Director eliminarDirector (Long id) {

        Director directorEliminado = obtenerPorId(id);

        directorEliminado.setActivo(false);

        return directorRepository.save(directorEliminado);
    }

}
