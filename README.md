# CineAPI - RESTful API de Películas y Directores con Spring Security & JWT

Este proyecto consiste en una API RESTful para la gestión de películas y directores, totalmente blindada mediante arquitectura stateless con JSON Web Tokens (JWT) y conectada a un cliente frontend nativo.

---

## Stack Tecnológico & Arquitectura

### Backend (Spring Boot)
*   **Core:** Spring Boot 4.x / Java 17
*   **Seguridad:** Spring Security & JJWT (Json Web Token v0.12.6)
*   **Persistencia:** Spring Data JPA / Hibernate
*   **Base de Datos:** PostgreSQL
*   **Productividad:** Lombok & Spring Boot DevTools
*   **Validación:** Jakarta Validation Starter

### Frontend (Vanilla Client)
*   **Estructura & Estilos:** HTML5 Semántico y CSS3 con Variables Nativas (Modo Oscuro)
*   **Lógica de Red:** JavaScript Moderno (ES6+) utilizando **Fetch API** con flujos asíncronos controlados mediante `async/await`.

---

## Decisiones de Diseño & Seguridad (Nivel Senior)

1. **Seguridad Stateless (Sin Estado):** Se deshabilitó la protección CSRF y las instancias de `HttpSession` debido a que la API es puramente Stateless. La identidad del usuario vuela efímeramente en cada petición mediante cabeceras `Authorization: Bearer <JWT>`.
2. **Mitigación del Problema de Rendimiento N+1:** Los repositorios de Spring Data JPA resuelven las consultas de listados pesados mediante JPQL personalizado utilizando la cláusula `LEFT JOIN FETCH`, reduciendo el impacto en PostgreSQL a una única consulta eficiente.
3. **Patrón Adaptador para Seguridad:** Se implementó la clase `UsuarioAdaptador` (que hereda de `UserDetails`) para desacoplar completamente las entidades relacionales de la base de datos del motor de autenticación interno de Spring Security.
4. **Idempotencia Forzada por Negocio:** Endpoint de registro aplica validación (`existsByUsername` / `existsByEmail`) para evitar la duplicidad de registros ante reenvíos accidentales de red, garantizando la consistencia del sistema.
5. **Políticas de CORS Centralizadas:** Configuración granular de intercambio de orígenes directamente en el `SecurityFilterChain` para permitir que el cliente frontend (`file://` o entornos locales) negocie cabeceras y métodos destructivos (OPTIONS, PUT, DELETE) de manera transparente con el backend.
6. **Manejo Semántico de Errores:** Centralizado globalmente mediante un `@ControllerAdvice`. Las excepciones de recursos no encontrados (`RecursoNoEncontradoException`) devuelven JSONs limpios con estados HTTP 404, mientras que los accesos anónimos a rutas ADMIN son interceptados por un `AuthenticationEntryPoint` personalizado que muestra un código HTTP 401 Unauthorized estructurado.

---

## Modelo de Endpoints de la API

Toda la API se encuentra estandarizada bajo el prefijo profesional `/api/`.

### Rutas Públicas (PermitAll)
*   `POST /api/auth/nuevo` -> Registro de nuevos usuarios (Contraseñas trituradas con Hash irreversible BCrypt).
*   `POST /api/auth/login` -> Intercambio de credenciales por el salvoconducto criptográfico (Genera el JWT).
*   `GET /api/peliculas` -> Recupera el catálogo completo de películas activas.
*   `GET /api/peliculas/{id}` -> Busca una película específica por su Identificador Único.

### Rutas Privadas (Exigen ROLE_ADMIN)
*   `POST /api/peliculas` -> Registra una nueva película vinculándola jerárquicamente con un objeto Director anidado.
*   `PUT /api/peliculas/{id}` -> Modificación integral del registro de la película.
*   `DELETE /api/peliculas/{id}` -> Ejecución de baja o borrado lógico (`activo = false`) preservando la integridad referencial.

---

## Estructura del Repositorio

El proyecto implementa un diseño limpio monorrepositorio para facilitar el despliegue de pruebas:

```text
├── src/main/java/              # Código fuente de Spring Boot
│   ├── controller/             # Controladores REST CRUD
│   ├── model/                  # Entidades de Hibernate (Pelicula, Director, Usuario, Rol)
│   ├── repository/             # Interfaces de Spring Data JPA
│   ├── security/               # El núcleo defensivo (Filtros, Configuración, Proveedores JWT)
│   └── util/                   # Sembrado idempotente de datos (CommandLineRunner)
├── frontend/                   # Interfaz de Usuario Desacoplada
│   ├── index.html              # Panel visual con acciones Fetch (GET, POST, PUT, DELETE)
│   └── fetch.html              # Enciclopedia técnica personal sobre asincronía y promesas
├── pom.xml                     # Gestión de dependencias de Maven
└── README.md                   # Documentación del sistema
```

---

## Cómo Arrancar el Proyecto

### Requisitos Previos
*   Java 17 instalado.
*   PostgreSQL corriendo localmente.
*   Tener creada una base de datos vacía llamada `db_peliculas`.

### Paso 1: Configurar Variables de Entorno
Para proteger las credenciales en producción, el proyecto oculta las claves en el `application.properties`. Configura tus variables de entorno locales:
```bash
PASSWORD_DB=tu_contraseña_de_postgres
```

### Paso 2: Levantar el Backend
Compila e inicia la aplicación de Spring Boot desde tu IDE o mediante la terminal:
```bash
mvn clean spring-boot:run
```
Al arrancar por primera vez, el sistema ejecutará un sembrado automático que inicializará los roles (`ROLE_USER` y `ROLE_ADMIN`) y los datos de prueba de forma segura.

### Paso 3: Lanzar el Frontend
1. Ve a la carpeta `frontend/`.
2. Haz doble clic sobre el archivo `index.html` para abrir el panel visual en tu navegador.
3. ¡Comienza a probar el programa! Registra un usuario, inicia sesión para adquirir tu token y experimenta con el CRUD protegido de películas.
