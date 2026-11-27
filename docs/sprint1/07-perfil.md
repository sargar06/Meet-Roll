# Paso 7. Ver y editar el perfil (CU-04)

**Issue:** #17 · **Rama:** `feature/17-perfil` · **Fecha:** 27/11/2026

## Objetivo

Que el usuario que ha iniciado sesión pueda ver sus datos y cambiar su nombre, foto y biografía.

| Petición | Qué hace | Respuestas |
|---|---|---|
| `GET /api/perfil` | Devuelve los datos del usuario | 200, 401 |
| `PUT /api/perfil` con `{ "nombre", "fotoUrl", "bio" }` | Cambia el perfil y devuelve los datos nuevos | 200, 400, 401 |

Las dos necesitan la cabecera `Authorization: Bearer <token>` obtenida en el inicio de sesión (paso 6). El correo
y el rol de organizador no se pueden cambiar desde el perfil.

## Cómo sabe el servidor de quién es el perfil

La ruta es `/api/perfil`, sin ningún id. El controlador hace siempre lo mismo al principio:

```java
Integer idUsuario = sesionService.usuarioDeLaCabecera(cabecera);
if (idUsuario == null) {
    return sinSesion();   // 401
}
```

Si el token no existe en el mapa de sesiones (no se envió, es inventado o ya se cerró la sesión), responde 401 y
no sigue. Si existe, usa **ese** id. Un usuario no tiene forma de pedir el perfil de otro.

## Archivos

| Paquete | Clase | Cambio |
|---|---|---|
| `o0dto` | `PerfilDTO` | Nuevo: `{ nombre, fotoUrl, bio }` |
| `o2service` | `UsuarioService` | Nuevos métodos `obtener(id)` y `actualizarPerfil(id, datos)` |
| `o1controller` | `PerfilController` | Nuevo: `GET` y `PUT` en `/api/perfil` |

Anotaciones nuevas: `@GetMapping` y `@PutMapping` (como `@PostMapping`, pero para `GET` y `PUT`) y
`@RequestHeader("Authorization")`, que lee una cabecera de la petición.

### Actualizar con `save`

`actualizarPerfil` busca el usuario con `findById`, cambia sus atributos con los setters y llama a `save`. Como el
objeto ya tiene id, Hibernate hace un `UPDATE` en lugar de un `INSERT`:

```sql
update usuario set bio=?,email=?,fecha_registro=?,foto_url=?,nombre=?,es_organizador=?,password_hash=? where id=?
```

Es el equivalente al `edit(empleado)` de los `JpaController` de clase.

### Comprobaciones

| Campo | Regla | Si falla |
|---|---|---|
| `nombre` | Obligatorio, 100 caracteres como máximo | 400 |
| `fotoUrl` | Opcional, 255 caracteres como máximo | 400 |
| `bio` | Opcional, 500 caracteres como máximo | 400 |

Los límites son los de las columnas en `schema.sql`.

## Pruebas (27/11/2026, con `curl`)

Tras iniciar sesión con el usuario de prueba:

| Prueba | Respuesta |
|---|---|
| 1. Ver perfil con token | **200** con los datos del usuario 1 |
| 2. Ver perfil sin token | **401** `{"error":"Hay que iniciar sesión"}` |
| 3. Ver perfil con un token inventado | **401** |
| 4. Editar nombre y biografía | **200** con `"nombre":"Usuario editado"` y la biografía nueva |
| 5. Editar con el nombre vacío | **400** con el motivo |
| 6. Editar sin token | **401** (no se cambia nada) |
| 7. Cerrar sesión y volver a pedir el perfil con el mismo token | **401**: el token ya no vale |
