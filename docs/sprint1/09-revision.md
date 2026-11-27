# Paso 9. Revisión del Sprint 1

**Issue:** #19 · **Rama:** `feature/19-revision` · **Fecha:** 27/11/2026

Comprobación final del sprint antes de fusionar las ramas en `main` y poner la etiqueta `sprint-1`.

## 1. Casos de uso

| Caso de uso | Petición | Respuestas | Comprobado con |
|---|---|---|---|
| CU-01 Registrarse | `POST /api/auth/registro` | 201 creado · 400 datos no válidos · 409 correo repetido | Postman 01, 02, 03 |
| CU-02 Iniciar sesión | `POST /api/auth/login` · `POST /api/auth/logout` | 200 con token · 401 credenciales incorrectas · 204 sesión cerrada | Postman 04, 05, 09 · `SesionServiceTest` |
| CU-04 Gestionar perfil | `GET /api/perfil` · `PUT /api/perfil` | 200 · 400 datos no válidos · 401 sin sesión | Postman 06, 07, 08, 10 |

## 2. Lista de comprobación

| Comprobación | Resultado |
|---|---|
| La base de datos se crea con `docs/schema.sql` (4 tablas) | Sí |
| El servidor arranca y conecta a MySQL (`ddl-auto=validate` no da errores) | Sí |
| `mvn test`: 10 tests JUnit | 10 correctos |
| Colección de Postman ejecutada con el Runner | 16 comprobaciones, 0 fallos |
| Las contraseñas se guardan cifradas con BCrypt y nunca se devuelven | Sí |
| El perfil se identifica por el token, no por un id que mande el cliente | Sí |
| `config/application.properties` (contraseña de MySQL) no está en el repositorio | Sí, está en `.gitignore` |
| `schema.sql` lleva `'CAMBIAR'` en lugar de la contraseña real | Sí |
| Cada issue tiene su rama y su commit con `Closes #n` | Sí (#14 a #19) |
| Documentación de cada paso en `docs/sprint1/` | Sí |
| Cambios respecto al diseño apuntados en `docs/CAMBIOS.md` | Sí |

## 3. Limitaciones conocidas

- **Las sesiones se pierden al reiniciar el servidor.** Los tokens se guardan en memoria (`ConcurrentHashMap`);
  al apagar el servidor hay que volver a iniciar sesión. Es suficiente para el proyecto.
- **Los tokens no caducan** mientras el servidor esté encendido; solo dejan de valer al cerrar sesión.
- **Cada ejecución de la colección de Postman crea un usuario** `postman<número>@meetroll.local`. Se pueden borrar
  en Workbench:

  ```sql
  DELETE FROM usuario WHERE email LIKE 'postman%@meetroll.local';
  ```

## 4. Cierre del sprint

Las ramas están encadenadas (cada una sale de la anterior), así que las pull requests se abren y se fusionan
**en orden**, una detrás de otra, todas con base `main`:

| Orden | Rama | Cierra |
|---|---|---|
| 1 | `feature/14-proyecto-base` | #14 |
| 2 | `feature/15-entidades-jpa` | #15 |
| 3 | `feature/16-registro-login` | #16 |
| 4 | `feature/17-perfil` | #17 |
| 5 | `feature/18-pruebas` | #18 |
| 6 | `feature/19-revision` | #19 |

Al fusionar se usa **«Create a merge commit»**, no «Squash and merge»: con *squash* los commits cambian y la
siguiente rama encadenada daría conflictos.

Cuando las seis estén en `main`, se pone la etiqueta que marca el final del sprint:

```powershell
git switch main
git pull
git tag -a sprint-1 -m "Fin del Sprint 1: registro, inicio de sesión y perfil"
git push origin sprint-1
```
