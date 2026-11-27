# Paso 5. Registro de usuario (CU-01)

**Issue:** #16 · **Rama:** `feature/16-registro-login` · **Fecha:** 27/11/2026

## Objetivo

Que un cliente pueda crear una cuenta enviando una petición al servidor:

```
POST http://localhost:8080/api/auth/registro
Content-Type: application/json

{ "nombre": "Ana", "email": "ana@correo.es", "password": "secreto123", "organizador": false }
```

## Recorrido de una petición

```
Cliente ──JSON──▶ AuthController ──RegistroDTO──▶ UsuarioService ──Usuario──▶ UsuarioRepository ──▶ MySQL
        ◀──JSON── (201 / 400 / 409) ◀──UsuarioDTO──
```

## Archivos creados

| Paquete | Clase | Qué hace |
|---|---|---|
| `o0dto` | `RegistroDTO` | `record` con lo que envía el cliente: nombre, email, password y organizador |
| `o0dto` | `UsuarioDTO` | `record` con lo que devuelve el servidor. Tiene un constructor `UsuarioDTO(Usuario u)` que convierte la entidad, como `EmpleadoDTO(Empleados e)` en clase. **No incluye la contraseña** |
| `o0dto` | `ErrorDTO` | Respuesta de error: `{ "error": "mensaje" }` |
| `o2service` | `UsuarioService` | Comprueba los datos, cifra la contraseña y guarda el usuario |
| `o1controller` | `AuthController` | Recibe la petición HTTP y devuelve la respuesta con su código |

Y en el `pom.xml` se añade `spring-security-crypto`: solo la parte de cifrado de Spring Security, para BCrypt.

## Qué comprueba el servicio

| Comprobación | Si falla |
|---|---|
| Nombre no vacío y de 100 caracteres como máximo (columna `VARCHAR(100)`) | 400 |
| Email con `@` y de 150 caracteres como máximo (columna `VARCHAR(150)`) | 400 |
| Contraseña de 8 a 72 caracteres (BCrypt solo usa los 72 primeros) | 400 |
| Que no exista ya una cuenta con ese correo | 409 |

El correo se guarda en minúsculas y sin espacios: `Ana@Correo.es` y `ana@correo.es` son la misma cuenta.

Los errores se señalan con dos excepciones de Java que el controlador captura con `try/catch`:

| Excepción | Significado | Código HTTP |
|---|---|---|
| `IllegalArgumentException` | Datos no válidos | 400 Bad Request |
| `IllegalStateException` | El correo ya existe | 409 Conflict |

## La contraseña: BCrypt

La contraseña nunca se guarda tal cual. `BCryptPasswordEncoder.encode("secreto123")` devuelve algo como
`$2a$10$N9qo8uLOickgx2ZMRZoMye...`. Características:

- **No se puede descifrar.** Para comprobar una contraseña en el inicio de sesión se usa
  `matches(contraseñaEscrita, hashGuardado)`.
- **Lleva «sal» aleatoria.** Cifrar dos veces la misma contraseña da resultados distintos, así que dos usuarios
  con la misma contraseña no tienen el mismo hash.
- **Es lenta a propósito** (el `10` del hash es el coste), para que probar millones de contraseñas lleve mucho
  tiempo.

Si alguien consiguiera una copia de la base de datos, no tendría las contraseñas.

## Anotaciones de Spring nuevas

| Anotación | Dónde | Qué hace |
|---|---|---|
| `@Service` | `UsuarioService` | Spring crea un único objeto de esta clase y lo pasa a quien lo pida en su constructor |
| `@RestController` | `AuthController` | Sus métodos responden a peticiones HTTP; lo que devuelven se envía en JSON |
| `@RequestMapping("/api/auth")` | `AuthController` | Prefijo de todas las rutas de la clase |
| `@PostMapping("/registro")` | método `registro` | Responde a `POST /api/auth/registro` |
| `@RequestBody` | parámetro `datos` | Convierte el JSON recibido en un `RegistroDTO` |

`ResponseEntity` permite elegir el código HTTP de la respuesta (`201`, `400`, `409`).

## Pruebas (27/11/2026, con `curl`)

| Prueba | Respuesta |
|---|---|
| Registro correcto | **201** `{"id":1,"nombre":"Usuario de prueba","email":"prueba@meetroll.local", ...}` sin contraseña |
| Mismo correo | **409** `{"error":"Ya existe una cuenta con ese correo"}` |
| Mismo correo en mayúsculas (`PRUEBA@MeetRoll.local`) | **409** |
| Nombre vacío, correo sin `@` y contraseña corta | **400** `{"error":"El nombre es obligatorio..."}` |

SQL generado por Hibernate (aparece en la consola gracias a `spring.jpa.show-sql=true`):

```sql
select u1_0.id from usuario u1_0 where u1_0.email=? limit ?          -- existsByEmail
insert into usuario (bio,email,fecha_registro,foto_url,nombre,es_organizador,password_hash) values (?,?,?,?,?,?,?)  -- save
```

Queda en la base de datos el usuario `prueba@meetroll.local` (contraseña `secreto123`) para probar el inicio de
sesión. Es un usuario de pruebas; se puede borrar con `DELETE FROM meetroll.usuario WHERE id = 1;`.

## Cómo probarlo a mano

Con el servidor arrancado (`mvn spring-boot:run` desde `servidor/`), en PowerShell:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/auth/registro -ContentType "application/json" -Body '{"nombre":"Ana","email":"ana@correo.es","password":"secreto123","organizador":false}'
```

O con Postman (paso 8).
