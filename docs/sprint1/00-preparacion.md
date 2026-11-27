# Paso 0. Preparación

**Fecha:** 26/11/2026

## Qué se ha hecho

1. Se han revisado los repositorios de clase (Acceso a Datos, Frameworks, Servicios y Procesos, Interfaces y
   Móviles) para saber qué técnicas se han visto y ajustar el código del proyecto a ese nivel.
2. Se han borrado las cinco ramas locales de la primera versión del sprint (`feature/14` a `feature/18`). No se
   habían subido a GitHub, así que no queda rastro de ellas en el repositorio remoto.
3. Se ha borrado la carpeta `servidor/`, que solo contenía la salida de compilación de Maven (`target/`).
4. Se ha creado esta carpeta de documentación.

## Qué se ha visto en clase y cómo se aplica

| Módulo | Qué se ha visto | Dónde se usa en Meet&Roll |
|---|---|---|
| Acceso a Datos | JDBC, JPA (entidades, `EntityManager`, consultas), DTOs como `record`, capas controller/service/repository | Entidades y acceso a la base de datos del servidor |
| Acceso a Datos | JavaFX con FXML y MVVM | Cliente de escritorio (Sprint 2) |
| Frameworks | HTML, CSS, Bootstrap, Angular | Cliente web (Sprint 3) |
| Móviles | Kotlin, Jetpack Compose, Room, MVVM | Cliente Android (Sprint 4) |
| Servicios y Procesos | Procesos e hilos | Chat (Sprint 3) |

Lo que no se ha visto y hace falta para el servidor es **Spring Boot**. Se usará solo lo necesario para exponer
una API a la que puedan llamar los tres clientes. Cada parte se explicará en el paso donde aparezca.

## Estado del equipo

| Herramienta | Versión | Estado |
|---|---|---|
| JDK | 21.0.9 | Instalado. La memoria indica Java 25; el proyecto se configura con 21 y se cambia cuando se instale el 25 |
| Maven | 3.9.15 | Instalado |
| MySQL | 8.4 | **Falta instalarlo** (paso 1) |
| Postman | — | Comprobar en el paso 8 |
