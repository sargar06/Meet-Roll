# Paso 8. Pruebas con JUnit y Postman

**Issue:** #18 · **Rama:** `feature/18-pruebas` · **Fecha:** 27/11/2026

Se prueba el servidor de dos formas que se complementan:

| | JUnit | Postman |
|---|---|---|
| Qué prueba | Clases sueltas, sin servidor ni base de datos | El servidor completo: HTTP → controlador → servicio → MySQL |
| Cuándo se ejecuta | `mvn test` (también al compilar con `mvn package`) | Con el servidor arrancado, desde Postman |
| Tipo de prueba | Unitaria | De integración / funcional |

## 1. Tests JUnit

En `servidor/src/test/java/com/meetroll/servidor/o2service/`. Solo prueban clases que se pueden crear con `new`,
por eso no hace falta Mockito ni base de datos.

### `SesionServiceTest` (6 tests)

| Test | Comprueba que… |
|---|---|
| `conElTokenSeObtieneElUsuario` | Con `Bearer <token>` se recupera el id con el que se creó la sesión |
| `cadaSesionTieneUnTokenDistinto` | Dos inicios de sesión del mismo usuario dan tokens distintos |
| `sinCabeceraNoHayUsuario` | Sin cabecera `Authorization` devuelve `null` |
| `conUnTokenInventadoNoHayUsuario` | Un token que no está en el mapa devuelve `null` |
| `sinLaPalabraBearerNoHayUsuario` | El token sin el prefijo `Bearer ` no se acepta |
| `despuesDeCerrarSesionElTokenNoVale` | Tras `cerrarSesion`, el token devuelve `null` |

### `CifradoContrasenaTest` (4 tests)

| Test | Comprueba que… |
|---|---|
| `laContrasenaCifradaNoEsLaOriginal` | `encode("secreto123")` no devuelve `"secreto123"` |
| `laContrasenaCorrectaCoincide` | `matches` acepta la contraseña buena |
| `unaContrasenaIncorrectaNoCoincide` | `matches` rechaza otra contraseña |
| `cifrarDosVecesLaMismaContrasenaDaResultadosDistintos` | La sal aleatoria hace que el hash cambie cada vez |

### Estructura de un test

```java
@Test
void despuesDeCerrarSesionElTokenNoVale() {
    String token = sesiones.crearSesion(3);           // 1. Preparar

    sesiones.cerrarSesion("Bearer " + token);          // 2. Ejecutar

    assertNull(sesiones.usuarioDeLaCabecera("Bearer " + token));   // 3. Comprobar
}
```

- `@Test` marca el método como test.
- `@BeforeEach` se ejecuta antes de cada test (crea un `SesionService` nuevo para que no se mezclen).
- `assertEquals`, `assertNull`, `assertTrue`… hacen fallar el test si no se cumple la condición.

Para JUnit se añade al `pom.xml` `spring-boot-starter-test` con `<scope>test</scope>`: solo se usa al ejecutar
los tests, no forma parte del servidor.

### Ejecutar

```powershell
cd servidor
mvn test
```

Resultado el 27/11/2026: `Tests run: 10, Failures: 0, Errors: 0` → `BUILD SUCCESS`.

## 2. Colección de Postman

Archivo: `servidor/postman/MeetRoll_Sprint1.postman_collection.json`. Diez peticiones que se ejecutan en orden;
cada una comprueba su código de respuesta:

| # | Petición | Esperado |
|---|---|---|
| 01 | Registro correcto (con un correo nuevo cada vez) | 201, sin contraseña en la respuesta |
| 02 | Registro con el mismo correo | 409 |
| 03 | Registro con datos no válidos | 400 |
| 04 | Login correcto (guarda el token en `{{token}}`) | 200 |
| 05 | Login con contraseña incorrecta | 401 |
| 06 | Ver perfil con el token | 200 |
| 07 | Editar perfil | 200 |
| 08 | Ver perfil sin token | 401 |
| 09 | Cerrar sesión | 204 |
| 10 | Ver perfil con el token ya cerrado | 401 |

Usa tres variables de colección: `baseUrl` (`http://localhost:8080`), `email` (la crea la petición 01 con la hora
actual, para no repetir correo) y `token` (lo guarda la 04 y lo usan la 06, 07, 09 y 10).

### Instalar Postman y ejecutar la colección

1. Descargar Postman desde la web oficial, https://www.postman.com/downloads/, e instalarlo. Para usarlo en
   local no hace falta cuenta («Continue without an account» / «Skip» si aparece).
2. Arrancar el servidor: `mvn spring-boot:run` desde `servidor/`.
3. En Postman: **Import** → seleccionar `MeetRoll_Sprint1.postman_collection.json`.
4. Sobre la colección, **Run** (*Run collection*) → **Run Meet&Roll - Sprint 1**.
5. Deben salir las 10 peticiones con sus comprobaciones en verde.

### Comprobación previa

Antes de abrirla en Postman, se ejecutaron las 10 peticiones del archivo con un script de Node.js contra el
servidor y MySQL (27/11/2026): **las 10 devuelven el código esperado**. Cada ejecución deja en la tabla `usuario`
un usuario `postman<número>@meetroll.local`; son datos de prueba que se pueden borrar.
