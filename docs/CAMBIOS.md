# Registro de cambios respecto al diseño inicial

El proyecto puede cambiar mientras se desarrolla si aparecen complicaciones. Cada cambio se apunta aquí con su
motivo y con lo que hay que actualizar en la memoria, para que el diseño y el código no se contradigan en la
entrega final.

| Fecha | Sprint | Cambio | Motivo | Qué hay que actualizar en la memoria |
|---|---|---|---|---|
| 26/11/2026 | 1 | Se rehace el Sprint 1 con código más sencillo, ajustado a lo visto en clase | La primera versión usaba Spring Security, JWT y Mockito, que no se han dado | — |
| 26/11/2026 | 1 | Java 21 en lugar de Java 25 (provisional) | Es el JDK instalado en el equipo; se cambia al instalar el 25 | Tabla de tecnologías, si no se llega a cambiar |
| 27/11/2026 | 1 | **JWT sustituido por un token de sesión aleatorio** (UUID guardado en un `ConcurrentHashMap` del servidor) | JWT necesita Spring Security y OAuth2, no vistos en clase. El token cumple lo mismo: el usuario se identifica por el token, nunca por un id del cliente | Tabla de tecnologías (quitar JWT y Spring Security / OAuth2 Resource Server), apartado 8.4 (cambiar «el `sub` del JWT» por «el token de sesión») y diagrama de clases si nombra JWT |
| 27/11/2026 | 1 | Solo `spring-security-crypto` (BCrypt), sin Spring Security completo | Basta para cifrar contraseñas | Tabla de tecnologías |
| 27/11/2026 | 1 | Validación de datos con `if` en el servicio, sin Bean Validation (`@NotBlank`, `@Email`) | Más fácil de seguir; es como se hace en clase | — |
| 27/11/2026 | 1 | Paquetes numerados como en Acceso a Datos (`o0dto`, `o1controller`, `o2service`, `o3repository`) | Es la organización vista en clase | Apartado 10 (Codificación) |
| 27/11/2026 | 1 | La comprobación «destinatario o evento» de `Mensaje` se deja en el `CHECK` de MySQL y en el servicio del chat (Sprint 3), no en la entidad | Simplifica la entidad; el chat no se usa hasta el Sprint 3 | — |
| 27/11/2026 | 1 | Pruebas: JUnit solo para clases que no usan la base de datos (`SesionService`, BCrypt), sin Mockito; el resto se prueba con la colección de Postman contra el servidor | Mockito no se ha visto en clase; Postman prueba el servidor completo | Apartado de pruebas |
| 27/11/2026 | 1 | Las sesiones se guardan en memoria: se pierden al reiniciar el servidor y no caducan | Consecuencia de quitar JWT; suficiente para el proyecto | Apartado 8.4, como limitación |
| 27/11/2026 | 1 | Rama `feature/16-registro-login` en lugar de `feature/16-registro-login-jwt` | Ya no se usa JWT | — |
| 26/11/2026 | 1 | MySQL Workbench 26.10 para gestionar la base de datos | Facilita ver tablas y ejecutar scripts sin la consola | Apartado de recursos y herramientas |

## Pendientes de decidir

- Nada por ahora.
