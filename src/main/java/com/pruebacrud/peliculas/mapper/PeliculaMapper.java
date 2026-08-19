package com.pruebacrud.peliculas.mapper;


import com.pruebacrud.peliculas.dto.DirectorDto;
import com.pruebacrud.peliculas.dto.PeliculaDto;
import com.pruebacrud.peliculas.dto.PeliculaResponseDto;
import com.pruebacrud.peliculas.model.Director;
import com.pruebacrud.peliculas.model.Pelicula;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// 'componentModel = spring' registra esta interfaz como un @Component en el contenedor IoC
@Mapper(componentModel = "spring")
public interface PeliculaMapper {

    // Explicación del Mapping: Toma "idDirector" del DTO y asígnalo a "director.id" en la Entidad
    @Mapping(target = "director.id", source = "idDirector")
    @Mapping(target = "id", ignore = true)     // <-- Le dices a MapStruct que ignore el ID
    @Mapping(target = "activo", ignore = true) // <-- Le dices que ignore el campo activo
    Pelicula toEntity(PeliculaDto dto);

    // 2. Para responder (Mapea automáticamente toda la entidad a tu DTO con nombre de director)
    PeliculaResponseDto toResponseDto(Pelicula entity);

    // 3. MapStruct usará este método internamente para rellenar el DirectorResumenDto automáticamente
    DirectorDto toDirectorDto(Director director);

}
