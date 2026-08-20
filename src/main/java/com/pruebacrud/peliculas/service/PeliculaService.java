package com.pruebacrud.peliculas.service;


import com.pruebacrud.peliculas.dto.PeliculaDto;
import com.pruebacrud.peliculas.dto.PeliculaResponseDto;
import com.pruebacrud.peliculas.exception.RecursoNoEncontradoException;
import com.pruebacrud.peliculas.mapper.PeliculaMapper;
import com.pruebacrud.peliculas.model.Pelicula;
import com.pruebacrud.peliculas.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<PeliculaResponseDto> listarPeliculas(Pageable pageable) {
        //return peliculaRepository.findAllPeliculasActivasOptimizado();
        /*
        List<PeliculaResponseDto> listado = peliculaRepository.findAllPeliculasActivasOptimizado().stream().map(pelicula -> peliculaMapper.toResponseDto(pelicula)).collect(Collectors.toList());
        return listado;
         */


        // Enviamos el objeto 'pageable' al repositorio.
        // SQL resultante automático: "SELECT * FROM peliculas LIMIT 10 OFFSET 0"
        Page<Pelicula> paginaEntidades = peliculaRepository.findAllPeliculasActivasOptimizado(pageable);

        // Transformamos la página de entidades a una página de DTOs usando MapStruct
        return paginaEntidades.map(peliculaMapper::toResponseDto);
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

    @Transactional(readOnly = true)
    public Page<PeliculaResponseDto> listarPeliculaPorFecha(Integer fechaExacta, Pageable pageable) {
        Page<Pelicula> listado = peliculaRepository.findAllPeliculasPorFecha(fechaExacta, pageable);
        return listado.map(peliculaMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<PeliculaResponseDto> listarPeliculaPorDuracion (Integer duracion, Pageable pageable) {
        Page<Pelicula> listado = peliculaRepository.findAllDuracionPelicula(duracion, pageable);
        return listado.map(peliculaMapper::toResponseDto);
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
