# Sprint 1: servidor y base de datos

Del 28/10/2026 al 28/11/2026. Issues #14 a #19. Casos de uso CU-01 (registrarse), CU-02 (iniciar sesión) y
CU-04 (gestionar perfil).

Esta carpeta recoge, paso a paso, qué se ha hecho en el sprint, por qué y cómo comprobarlo. Cada paso tiene su
propio archivo.

## Decisiones

| Decisión | Motivo |
|---|---|
| Se rehace el sprint desde cero con código más sencillo | La primera versión usaba Spring Security, JWT y Mockito, que no se han visto en clase |
| Base de datos MySQL 8.4 | Es la que figura en la memoria y en el diagrama relacional |
| Spring Boot solo para lo imprescindible | Los tres clientes necesitan un servidor al que llamar por la red. Se usan controladores, servicios, repositorios y entidades JPA, con la misma organización por capas que en Acceso a Datos |
| Un paso cada vez, documentado | Para poder explicar y defender cada parte del código |

## Pasos

| Paso | Issue | Qué se hace | Estado |
|---|---|---|---|
| [0](00-preparacion.md) | — | Limpiar el repositorio y preparar esta documentación | Hecho |
| [1](01-mysql.md) | #14 | Instalar MySQL 8.4 y crear la base de datos con `schema.sql` | Hecho |
| [2](02-proyecto-spring-boot.md) | #14 | Crear el proyecto Spring Boot y conectarlo a MySQL | Hecho |
| [3 y 4](03-entidades-repositorios.md) | #15 | Entidades JPA (`Usuario`, `Evento`, `TipoEvento`, `Inscripcion`, `Mensaje`) y repositorios | Hecho |
| [5](05-registro.md) | #16 | Registro de usuario (CU-01) | Hecho |
| [6](06-inicio-sesion.md) | #16 | Inicio de sesión (CU-02) con token de sesión | Hecho |
| 7 | #17 | Ver y editar el perfil (CU-04) | Pendiente |
| 8 | #18 | Pruebas con Postman y JUnit | Pendiente |
| 9 | #19 | Revisión del sprint y etiqueta `sprint-1` | Pendiente |

## Ramas

Una rama por issue, creada desde `main`: `feature/14-proyecto-base`, `feature/15-entidades-jpa`,
`feature/16-registro-login`, `feature/17-perfil` y `feature/18-pruebas`. Cada una se cierra con una pull request
que incluye `Closes #n`.

Cada rama sale de la anterior (la #15 de la #14, etc.), porque necesita su código. Las pull requests se fusionan en
orden; si se abre una antes de fusionar la anterior, su rama base debe ser la de la issue anterior.
