package com.pruebacrud.peliculas.config;

import com.pruebacrud.peliculas.model.Director;
import com.pruebacrud.peliculas.model.Pelicula;
import com.pruebacrud.peliculas.repository.DirectorRepository;
import com.pruebacrud.peliculas.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class InicializadorDatos implements CommandLineRunner {


    private final PeliculaRepository peliculaRepository;
    private final DirectorRepository directorRepository;

    @Override
    public void run(String... args) throws Exception {

        if(peliculaRepository.count()==0 && directorRepository.count()==0) {
            Director nolan = new Director();
            nolan.setNombre("Christopher Nolan");
            nolan.setNacionalidad("Británica");
            directorRepository.save(nolan);

            Director tarantino = new Director();
            tarantino.setNombre("Quentin Tarantino");
            tarantino.setNacionalidad("Estadounidense");
            directorRepository.save(tarantino);


            Pelicula p1 = new Pelicula();
            p1.setTitulo("Inception");
            p1.setFechaLanzamiento(2010);
            p1.setDuracion(148);
            p1.setActivo(true);
            p1.setDirector(nolan);
            peliculaRepository.save(p1);

            Pelicula p2 = new Pelicula();
            p2.setTitulo("Interstellar");
            p2.setFechaLanzamiento(2014);
            p2.setDuracion(169);
            p2.setActivo(true);
            p2.setDirector(nolan);
            peliculaRepository.save(p2);

            Pelicula p3 = new Pelicula();
            p3.setTitulo("Pulp Fiction");
            p3.setFechaLanzamiento(1994);
            p3.setDuracion(154);
            p3.setActivo(true);
            p3.setDirector(tarantino);
            peliculaRepository.save(p3);

            System.out.println("¡Base de datos inicializada con éxito con directores y películas de prueba!");
        }



    }
}
