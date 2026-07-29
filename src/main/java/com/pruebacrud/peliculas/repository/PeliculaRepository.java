package com.pruebacrud.peliculas.repository;

import com.pruebacrud.peliculas.model.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {

    @Query("SELECT p FROM Pelicula p JOIN FETCH p.director WHERE p.activo = true")
    List<Pelicula> findAllPeliculasActivasOptimizado();

    @Query("SELECT p FROM Pelicula p WHERE p.fechaLanzamiento = :fechaExacta AND p.activo = true")
    List<Pelicula> findAllPeliculasPorFecha(@Param("fechaExacta") Integer fechaExacta);

    @Query("SELECT p FROM Pelicula p WHERE p.duracion < :duracionMax AND p.activo = true")
    List<Pelicula> findAllDuracionPelicula(@Param("duracionMax") Integer duracionMax);


    /*
    // Esto hace exactamente lo mismo que tus dos @Query anteriores:
    List<Pelicula> findByFechaLanzamientoAndActivoTrue(Integer fechaLanzamiento);
    List<Pelicula> findByDuracionLessThanAndActivoTrue(Integer duracionMax);

     */

}
