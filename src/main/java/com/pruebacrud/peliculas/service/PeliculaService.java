package com.pruebacrud.peliculas.service;


import com.pruebacrud.peliculas.exception.RecursoNoEncontradoException;
import com.pruebacrud.peliculas.model.Pelicula;
import com.pruebacrud.peliculas.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PeliculaService {

    private final PeliculaRepository peliculaRepository;


    public List<Pelicula> listarPeliculas() {
        return peliculaRepository.findAllPeliculasActivasOptimizado();
    }

    public Pelicula obtenerPorId (Long id) {
        Pelicula pelicula = peliculaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("La pelicula con ID " + id + " no existe."));

        if (!pelicula.getActivo()) {
            throw new RecursoNoEncontradoException("La pelicula con ID " + id + " ha sido dada de baja.");
        }

        return pelicula;
    }

    public List<Pelicula> listarPeliculaPorFecha(Integer fechaExacta) {
        return peliculaRepository.findAllPeliculasPorFecha(fechaExacta);
    }

    public List<Pelicula> listarPeliculaPorDuracion (Integer duracion) {
        return peliculaRepository.findAllDuracionPelicula(duracion);
    }


    public Pelicula guardarPelicula (Pelicula nuevaPelicula) {

        nuevaPelicula.setId(null);
        nuevaPelicula.setActivo(true);
        return peliculaRepository.save(nuevaPelicula);

    }

    public Pelicula actualizarPelicula (Long id, Pelicula peliculaModificada) {

        Pelicula peliculaExistente = obtenerPorId(id);

        peliculaExistente.setDuracion(peliculaModificada.getDuracion());
        peliculaExistente.setDirector(peliculaModificada.getDirector());
        peliculaExistente.setTitulo(peliculaModificada.getTitulo());
        peliculaExistente.setFechaLanzamiento(peliculaModificada.getFechaLanzamiento());

        return peliculaRepository.save(peliculaExistente);
    }

    public Pelicula eliminarPelicula (Long id) {
        Pelicula peliculaExistente = obtenerPorId(id);

        peliculaExistente.setActivo(false);

        return peliculaRepository.save(peliculaExistente);
    }

}
