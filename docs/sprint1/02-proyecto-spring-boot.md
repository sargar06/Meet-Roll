# Paso 2. Proyecto Spring Boot conectado a MySQL

**Issue:** #14 · **Rama:** `feature/14-proyecto-base` · **Fecha:** 27/11/2026

## Objetivo

Crear el proyecto del servidor y comprobar que arranca y se conecta a la base de datos `meetroll` del paso 1.

## Qué es Spring Boot y qué hace por nosotros

En Acceso a Datos la conexión con JPA se hace a mano: los datos de conexión van en `persistence.xml` y la clase
`JPAUtil` crea el `EntityManagerFactory` y lo reutiliza. En el servidor de Meet&Roll eso lo hace Spring Boot:

| En clase (JPA sin Spring) | En Meet&Roll (Spring Boot) |
|---|---|
| `persistence.xml` con la URL, el usuario y la contraseña | `application.properties` |
| `JPAUtil` crea el `EntityManagerFactory` | Spring lo crea al arrancar |
| `new EmpleadosService()`, `new EmpleadosJpaController()` | Spring crea los servicios y repositorios y los pasa por el constructor |
| `em.getTransaction().begin()` / `commit()` | Se hace solo en cada método del repositorio |
| No hay servidor web | Arranca Tomcat en el puerto 8080 para que los clientes llamen a la API |

La organización por capas (controlador → servicio → repositorio → entidad) es la misma que en clase.

## Archivos creados

```
servidor/
├── pom.xml
├── config/
│   ├── application.properties          ← usuario y contraseña (NO se sube)
│   └── application.properties.example  ← plantilla sin contraseña (sí se sube)
└── src/main/
    ├── java/com/meetroll/servidor/
    │   └── ServidorApplication.java
    └── resources/
        └── application.properties
```

### `pom.xml`

Proyecto Maven con Spring Boot 4.1.3 y Java 21 (ver [CAMBIOS.md](../CAMBIOS.md)). El `parent` de Spring Boot fija
las versiones de las librerías, por eso las dependencias no llevan `<version>`. Solo hay tres:

| Dependencia | Para qué |
|---|---|
| `spring-boot-starter-webmvc` | Servidor Tomcat y controladores REST (`@RestController`) |
| `spring-boot-starter-data-jpa` | JPA con Hibernate: entidades y repositorios |
| `mysql-connector-j` | Driver JDBC de MySQL |

### `ServidorApplication.java`

El `main`. La anotación `@SpringBootApplication` hace que Spring busque en el paquete `com.meetroll.servidor` y
sus subpaquetes las clases marcadas con `@Entity`, `@Repository`, `@Service` o `@RestController` y las cree.

### `application.properties`

| Propiedad | Valor | Para qué |
|---|---|---|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/meetroll?serverTimezone=Europe/Madrid` | Dónde está la base de datos |
| `spring.jpa.hibernate.ddl-auto` | `validate` | Al arrancar, Hibernate comprueba que las entidades coinciden con las tablas. Nunca crea ni cambia tablas: eso lo hace `schema.sql` (y el usuario `meetroll` tampoco tiene permiso) |
| `spring.jpa.open-in-view` | `false` | La conexión se suelta en cuanto el servicio termina, no mientras se envía la respuesta |
| `spring.jpa.show-sql` | `true` | Muestra en la consola las consultas SQL que hace Hibernate |

### `config/application.properties`

Contiene `spring.datasource.username` y `spring.datasource.password`. Spring Boot lo lee automáticamente si el
servidor se arranca desde la carpeta `servidor/`, y sus valores se suman a los de `application.properties`. Está
en el `.gitignore`: en GitHub solo se sube la plantilla `application.properties.example`. Quien clone el
repositorio la copia y pone su contraseña.

## Cómo arrancar el servidor

```powershell
cd servidor
mvn spring-boot:run
```

Se para con **Ctrl+C**. Al pararlo, Maven puede mostrar `[ERROR] ... exit code: 143` (o 130): no es un fallo, solo
indica que el proceso se cortó a mano.

## Comprobación

Resultado del arranque el 27/11/2026:

```
HikariPool-1 - Start completed.
Database dialect: MySQLDialect
Database version: 8.4.13
Tomcat started on port 8080 (http) with context path '/'
Started ServidorApplication in 2.942 seconds
```

- `HikariPool-1 - Start completed`: se ha conectado a MySQL con el usuario `meetroll`. HikariCP es el *pool* de
  conexiones: mantiene varias abiertas y las reutiliza.
- `Tomcat started on port 8080`: el servidor ya atiende en `http://localhost:8080`.

## Problemas posibles

| Mensaje | Causa |
|---|---|
| `Access denied for user 'meetroll'@'localhost'` | La contraseña de `config/application.properties` no coincide con la del `ALTER USER`, o se ha arrancado desde otra carpeta y Spring no encuentra el archivo |
| `Communications link failure` | El servicio `MySQL84` está parado (`net start MySQL84` como administrador) |
| `Port 8080 was already in use` | Ya hay otro servidor arrancado; pararlo con Ctrl+C |
| Aviso `spring.jpa.open-in-view is enabled by default` | Ya resuelto con `spring.jpa.open-in-view=false` |
