package com.pruebacrud.peliculas.service;


import com.pruebacrud.peliculas.dto.PeliculaDto;
import com.pruebacrud.peliculas.dto.PeliculaResponseDto;
import com.pruebacrud.peliculas.exception.RecursoNoEncontradoException;
import com.pruebacrud.peliculas.mapper.PeliculaMapper;
import com.pruebacrud.peliculas.model.Pelicula;
import com.pruebacrud.peliculas.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PeliculaService {

    private final PeliculaRepository peliculaRepository;

    private final PeliculaMapper peliculaMapper;


    @Transactional(readOnly = true)
    public List<PeliculaResponseDto> listarPeliculas() {
        //return peliculaRepository.findAllPeliculasActivasOptimizado();
        List<PeliculaResponseDto> listado = peliculaRepository.findAllPeliculasActivasOptimizado().stream().map(pelicula -> peliculaMapper.toResponseDto(pelicula)).collect(Collectors.toList());
        return listado;
    }

    @Transactional(readOnly = true)
    private Pelicula obtenerPorId (Long id) {
        Pelicula pelicula = peliculaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("La pelicula con ID " + id + " no existe."));

        if (!pelicula.getActivo()) {
            throw new RecursoNoEncontradoException("La pelicula con ID " + id + " ha sido dada de baja.");
        }

        return pelicula;
    }

    @Transactional(readOnly = true) // readOnly mejora el rendimiento en búsquedas
    public PeliculaResponseDto obtenerPorIdDto(Long id) {

        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La película con ID " + id + " no existe."));

        if (!pelicula.getActivo()) {
            throw new RecursoNoEncontradoException("La película con ID " + id + " ha sido dada de baja.");
        }

        return peliculaMapper.toResponseDto(pelicula);
    }

    public List<PeliculaResponseDto> listarPeliculaPorFecha(Integer fechaExacta) {
        List<PeliculaResponseDto> listado = peliculaRepository.findAllPeliculasPorFecha(fechaExacta).stream().map(pelicula -> peliculaMapper.toResponseDto(pelicula)).collect(Collectors.toList());
        return listado;
    }

    public List<PeliculaResponseDto> listarPeliculaPorDuracion (Integer duracion) {
        List<PeliculaResponseDto> listado = peliculaRepository.findAllDuracionPelicula(duracion).stream().map(pelicula -> peliculaMapper.toResponseDto(pelicula)).collect(Collectors.toList());
        return listado;
    }


    @Transactional
    public PeliculaResponseDto guardarPelicula (PeliculaDto nuevaPeliculaDto) {

        Pelicula nuevaPelicula = peliculaMapper.toEntity(nuevaPeliculaDto);

        nuevaPelicula.setId(null);
        nuevaPelicula.setActivo(true);

        Pelicula peliculaGuardada = peliculaRepository.save(nuevaPelicula);

        return peliculaMapper.toResponseDto(peliculaGuardada);

    }

    @Transactional
    public PeliculaResponseDto actualizarPelicula (Long id, PeliculaDto peliculaModificadaDto) {

        Pelicula peliculaExistente = obtenerPorId(id);

        Pelicula datosNuevos = peliculaMapper.toEntity(peliculaModificadaDto);

        peliculaExistente.setDuracion(datosNuevos.getDuracion());
        peliculaExistente.setDirector(datosNuevos.getDirector());
        peliculaExistente.setTitulo(datosNuevos.getTitulo());
        peliculaExistente.setFechaLanzamiento(datosNuevos.getFechaLanzamiento());

        Pelicula peliculaGuardada = peliculaRepository.save(peliculaExistente);

        return peliculaMapper.toResponseDto(peliculaGuardada);
    }

    @Transactional
    public PeliculaResponseDto eliminarPelicula (Long id) {
        Pelicula peliculaExistente = obtenerPorId(id);

        peliculaExistente.setActivo(false);

        Pelicula peliculaEliminada = peliculaRepository.save(peliculaExistente);

        return peliculaMapper.toResponseDto(peliculaEliminada);
    }

}
