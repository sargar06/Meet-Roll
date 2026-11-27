# Paso 6. Inicio de sesión (CU-02)

**Issue:** #16 · **Rama:** `feature/16-registro-login` · **Fecha:** 27/11/2026

## El problema

HTTP no tiene memoria: cada petición llega sola. Después de iniciar sesión, cuando el cliente pide «mi perfil»,
el servidor necesita saber **quién** lo pide. No vale que el cliente mande su id (`/api/perfil/1`), porque
cualquiera podría cambiar el número y ver o editar el perfil de otro.

## La solución elegida: token de sesión

La memoria proponía JWT, pero JWT necesita Spring Security y OAuth2 Resource Server, que no se han visto en clase.
Se ha sustituido por un **token de sesión aleatorio**, que se explica con lo visto en clase (un `Map` y la clase
`UUID`) y cumple lo mismo que pedía la memoria: el servidor identifica al usuario por el token, nunca por un id
enviado por el cliente. El cambio está en [CAMBIOS.md](../CAMBIOS.md).

```
1. POST /api/auth/login   { "email": "...", "password": "..." }
   Servidor: comprueba la contraseña con BCrypt
             token = UUID.randomUUID()  →  "eb789d55-9228-44d1-aaa6-631b15a2ccf4"
             sesiones.put(token, idUsuario)
   ← 200 { "token": "eb789d55-...", "usuario": { "id": 1, ... } }

2. GET /api/perfil
   Authorization: Bearer eb789d55-...
   Servidor: idUsuario = sesiones.get(token)   →  1
   ← 200 { datos del usuario 1 }

3. POST /api/auth/logout
   Authorization: Bearer eb789d55-...
   Servidor: sesiones.remove(token)
   ← 204
```

| | Token de sesión (elegido) | JWT (memoria original) |
|---|---|---|
| Qué recibe el cliente | Un código aleatorio sin información | Un texto firmado con el id y el correo dentro |
| Dónde se guarda la sesión | En el servidor, en un `Map` | En ningún sitio: el servidor comprueba la firma |
| Cerrar sesión | Se borra del `Map` y deja de valer al momento | El token sigue valiendo hasta que caduca |
| Si se reinicia el servidor | Hay que volver a iniciar sesión | Los tokens siguen valiendo |
| Qué hace falta | `HashMap` y `UUID` | Spring Security, OAuth2, claves de firma |

## Archivos

| Paquete | Clase | Qué hace |
|---|---|---|
| `o0dto` | `LoginDTO` | `{ email, password }` que envía el cliente |
| `o0dto` | `SesionDTO` | `{ token, usuario }` que devuelve el servidor |
| `o2service` | `SesionService` | El `Map` de sesiones: `crearSesion`, `usuarioDeLaCabecera`, `cerrarSesion` |
| `o2service` | `UsuarioService` | Nuevo método `comprobarCredenciales` |
| `o1controller` | `AuthController` | Nuevas rutas `POST /api/auth/login` y `POST /api/auth/logout` |

### `SesionService` y los hilos

Tomcat atiende cada petición en un **hilo** distinto, así que dos usuarios que inician sesión a la vez escriben
en el mapa al mismo tiempo. Un `HashMap` normal puede corromperse en ese caso (lo visto en Servicios y Procesos con
`synchronized`). Por eso se usa `ConcurrentHashMap`, que está preparado para que lo usen varios hilos a la vez.

### La cabecera `Authorization`

Es la cabecera estándar de HTTP para enviar credenciales. Por convención el token va precedido de `Bearer `
(«portador»): `Authorization: Bearer eb789d55-...`. `SesionService` quita ese prefijo antes de buscar el token.

### Mismo error para correo y contraseña

Si el correo no existe o la contraseña está mal, la respuesta es la misma: 401 «Correo o contraseña incorrectos».
Si fueran distintas, alguien podría averiguar qué correos están registrados probando.

## Pruebas (27/11/2026, con `curl`)

| Prueba | Respuesta |
|---|---|
| Login correcto (`Prueba@MeetRoll.local`, en mayúsculas) | **200** `{"token":"eb789d55-...","usuario":{"id":1, ...}}` |
| Contraseña incorrecta | **401** `{"error":"Correo o contraseña incorrectos"}` |
| Correo que no existe | **401** con el mismo mensaje |
| Logout con el token | **204** |

## Limitaciones conocidas

- Las sesiones se pierden al reiniciar el servidor (están en memoria). Para un proyecto que se ejecuta en local es
  aceptable; los clientes vuelven a la pantalla de inicio de sesión si reciben un 401.
- Los tokens no caducan solos: duran hasta que el usuario cierra sesión o se reinicia el servidor. Una mejora
  posible es guardar también la hora de creación y rechazar los tokens de más de 8 horas.
