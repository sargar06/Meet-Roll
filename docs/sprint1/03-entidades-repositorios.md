# Pasos 3 y 4. Entidades JPA y repositorios

**Issue:** #15 · **Rama:** `feature/15-entidades-jpa` · **Fecha:** 27/11/2026

Los pasos 3 y 4 se han hecho juntos porque son cortos y van ligados: un repositorio no tiene sentido sin su
entidad.

## Organización en paquetes

Se sigue la misma organización numerada que en Acceso a Datos:

```
com.meetroll.servidor
├── ServidorApplication.java
├── o0dto/                  ← objetos que viajan entre el servidor y los clientes (paso 5)
├── o1controller/           ← reciben las peticiones HTTP (paso 5)
├── o2service/              ← lógica: comprobaciones antes de guardar (paso 5)
└── o3repository/           ← acceso a la base de datos
    ├── UsuarioRepository.java
    ├── EventoRepository.java
    ├── InscripcionRepository.java
    ├── MensajeRepository.java
    └── entidades/
        ├── TipoEvento.java
        ├── Usuario.java
        ├── Evento.java
        ├── Inscripcion.java
        └── Mensaje.java
```

Cada capa solo llama a la de abajo: el controlador al servicio, el servicio al repositorio. El cliente nunca toca
la base de datos.

## Entidades

Una entidad es una clase que representa una tabla; cada objeto es una fila. Igual que las clases `Empleados` y
`Departamentos` de clase.

| Anotación | Qué indica | Ejemplo |
|---|---|---|
| `@Entity` | La clase es una entidad JPA | `Usuario` |
| `@Table(name = "...")` | Tabla a la que corresponde | `usuario` |
| `@Id` | Clave primaria | `id` |
| `@GeneratedValue(strategy = IDENTITY)` | El id lo pone MySQL con `AUTO_INCREMENT` | `id` |
| `@Column(name = "...")` | Columna; `nullable`, `length`, `unique` reflejan las restricciones del SQL | `password_hash` |
| `@Enumerated(EnumType.STRING)` | Guarda el enum por su nombre (`"TCG"`) y no por su posición (`1`) | `Evento.tipo` |
| `@ManyToOne` + `@JoinColumn` | Clave foránea: en la tabla hay un id, en Java el objeto completo | `Evento.organizador` |

Correspondencia con las tablas:

| Tabla | Entidad | Claves foráneas |
|---|---|---|
| `usuario` | `Usuario` | — |
| `evento` | `Evento` (+ enum `TipoEvento`) | `organizador` → `Usuario` |
| `inscripcion` | `Inscripcion` | `usuario` → `Usuario`, `evento` → `Evento` |
| `mensaje` | `Mensaje` | `remitente` y `destinatario` → `Usuario`, `evento` → `Evento` |

Decisiones para mantenerlo sencillo:

- **Asociaciones solo en un sentido.** `Evento` conoce a su `Usuario` organizador, pero `Usuario` no tiene una
  lista de eventos. Es lo que indica el diagrama de clases de la memoria y evita bucles al convertir a JSON.
- **`@ManyToOne` sin `fetch`.** Por defecto es `EAGER`: al leer un evento se lee también su organizador. Es como
  en el ejemplo de clase y evita errores de carga perezosa.
- **Fechas con `LocalDateTime.now()`** en el propio atributo: al crear un `Usuario`, su `fechaRegistro` ya tiene
  la fecha actual.
- **Columnas `TEXT`** (`descripcion`, `contenido`) con `columnDefinition = "TEXT"`, para que la validación de
  Hibernate no espere un `VARCHAR`.
- **Mensaje privado o de grupo:** la regla «o destinatario o evento, pero no los dos» la garantiza el `CHECK` de
  MySQL. El servicio del chat (Sprint 3) la comprobará también antes de guardar, para devolver un error claro.

## Repositorios

En clase, cada `JpaController` tenía el código de `create`, `edit`, `destroy` y `find...` escrito a mano (lo
generaba NetBeans). Con Spring Data JPA basta con una **interfaz** que herede de `JpaRepository<Entidad, TipoId>`;
Spring escribe el código al arrancar.

| Clase de clase | Repositorio de Spring |
|---|---|
| `create(empleado)` / `edit(empleado)` | `save(usuario)` (inserta si no tiene id, actualiza si lo tiene) |
| `findEmpleados(id)` | `findById(id)` (devuelve `Optional`) |
| `findEmpleadosEntities()` | `findAll()` |
| `destroy(id)` | `deleteById(id)` |
| `@NamedQuery` | métodos con nombre `findBy...` |

`UsuarioRepository` añade dos consultas que Spring crea a partir del nombre del método:

| Método | Consulta que genera | Para qué |
|---|---|---|
| `findByEmail(String email)` | `SELECT * FROM usuario WHERE email = ?` | Inicio de sesión |
| `existsByEmail(String email)` | ¿hay alguna fila con ese email? | Registro: no repetir correos |

Los demás repositorios están vacíos por ahora; sus consultas se añaden en los sprints donde se usan.

## Comprobación

Con `spring.jpa.hibernate.ddl-auto=validate`, al arrancar Hibernate compara cada entidad con su tabla. Si una
columna no existe o su tipo no cuadra, el servidor no arranca y muestra `Schema-validation: missing column` o
`wrong column type`.

Resultado el 27/11/2026: el servidor arranca (`Started ServidorApplication in 4.318 seconds`) sin errores de
validación, así que las cinco clases coinciden con las cuatro tablas de `schema.sql`.
