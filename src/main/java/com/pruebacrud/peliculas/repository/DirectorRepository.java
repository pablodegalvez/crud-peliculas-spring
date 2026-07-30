package com.pruebacrud.peliculas.repository;


import com.pruebacrud.peliculas.model.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {

    @Query("SELECT d FROM Director d LEFT JOIN FETCH d.peliculaList WHERE d.activo = true")
    List<Director> findAllDirectorOptimizado();

    //List<Director> findByActivoTrue();

}
