package com.pruebacrud.peliculas.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ManejadorExcepciones {


    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarIdNoEncontrado(RecursoNoEncontradoException ex) {

        Map<String, Object> cuerpoError = new HashMap<>();
        cuerpoError.put("timestamp", LocalDateTime.now());
        cuerpoError.put("status", HttpStatus.NOT_FOUND.value()); // Código 404
        cuerpoError.put("error", "Recurso no localizado");
        cuerpoError.put("message", ex.getMessage()); // El mensaje dinámico que escribiste en el servicio

        // Devolvemos el JSON personalizado junto con el código de estado HTTP 404 real
        return new ResponseEntity<>(cuerpoError, HttpStatus.NOT_FOUND);

    }


}
