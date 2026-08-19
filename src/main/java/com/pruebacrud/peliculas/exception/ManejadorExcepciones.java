package com.pruebacrud.peliculas.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    // Capturamos específicamente la excepción de Jakarta Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        // 1. Extraemos todos los errores de los campos y los mapeamos a un Map<String, String>
        // Ejemplo de resultado: {"titulo": "El título es obligatorio", "duracion": "Debe ser mayor a cero"}
        Map<String, String> erroresCampos = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(), // Clave: nombre del atributo del DTO (ej: "titulo")
                        error -> Objects.toString(error.getDefaultMessage(), "Campo inválido"), // Valor: tu mensaje personalizado
                        (existing, replacement) -> existing // Combinador por si hay campos duplicados en el grafo
                ));

        // 2. Construimos la estructura del JSON corporativo final que viajará por la red
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.BAD_REQUEST.value()); // Código 400
        respuesta.put("error", "Bad Request");
        respuesta.put("errors", erroresCampos); // Metemos el sub-mapa con el desglose de los campos

        // 3. Devolvemos un HTTP 400 Bad Request
        return ResponseEntity.badRequest().body(respuesta);
    }


}
