# Paso 1. Instalar MySQL 8.4 y crear la base de datos

**Issue:** #14 · **Rama:** `feature/14-proyecto-base` · **Fecha:** 26/11/2026

## Objetivo

Tener MySQL 8.4 funcionando en el equipo y la base de datos `meetroll` creada con sus cuatro tablas
(`usuario`, `evento`, `inscripcion` y `mensaje`).

## 1. Descarga

Todo se descarga de la web oficial de MySQL (`dev.mysql.com`). Versiones comprobadas el 26/11/2026:

| Programa | Página de descarga | Archivo | Tamaño | MD5 |
|---|---|---|---|---|
| MySQL Community Server 8.4.13.1 LTS | https://dev.mysql.com/downloads/mysql/8.4.html | `mysql-8.4.13.1-winx64.msi` | 130 MB | `538ceec21051924233e72e35550dd4fa` |
| MySQL Workbench 26.10.0 (opcional) | https://dev.mysql.com/downloads/workbench/ | `mysql-workbench-26.10.0-winx64.msi` | 261 MB | `1987bb5a22f37d4603d651fb290c951d` |

En la página del servidor, comprobar que en «Select Version» está marcada **8.4.13.1 LTS** (la página también
ofrece las 9.x y 26.x, que no son las de la memoria) y elegir **Windows (x86, 64-bit), MSI Installer**. Al pulsar
«Download», la web pide iniciar sesión con una cuenta de Oracle; no hace falta: abajo está el enlace
**«No thanks, just start my download»**.

Workbench es un programa gráfico para ver las tablas y ejecutar SQL. No es obligatorio, pero es más cómodo que
la consola.

### Comprobar que el archivo no está dañado

En PowerShell, desde la carpeta de descargas:

```powershell
Get-FileHash .\mysql-8.4.13.1-winx64.msi -Algorithm MD5
```

El resultado tiene que coincidir con el MD5 de la tabla (PowerShell lo muestra en mayúsculas; da igual).
Desde el Símbolo del sistema (cmd), donde `Get-FileHash` no existe:

```
certutil -hashfile mysql-8.4.13.1-winx64.msi MD5
```

Al ejecutar el instalador, Windows pide permisos de administrador con un nombre raro (por ejemplo `9304b8.msi`):
es la copia que Windows Installer guarda en `C:\Windows\Installer`. Lo que hay que comprobar es que el editor sea
**Oracle**.

**Atención a la versión:** en la página general de descargas, cambiar el desplegable a 8.4 no siempre recarga la
página y se sigue descargando la 26.x. Hay que fijarse en que el título diga *MySQL Community Server 8.4.13.1 LTS*
y el archivo sea `mysql-8.4.13.1-winx64.msi`; lo más seguro es entrar directamente por el enlace de la tabla.

## 2. Instalación

1. Ejecutar el `.msi` y elegir el tipo de instalación **Typical**.
2. Al terminar se abre **MySQL Configurator** (si no se abre, buscarlo en el menú Inicio):
   - *Type and Networking*: **Development Computer**, puerto **3306**, y **desmarcar** «Open Windows Firewall
     ports for network access». A MySQL solo se conecta el servidor Spring Boot, que está en el mismo equipo; los
     clientes (también el móvil) hablan con el servidor, no con la base de datos. Así nadie de la misma red wifi
     puede intentar conectarse a MySQL.
   - *Accounts and Roles*: escribir una contraseña para **root**. Guardarla en un sitio seguro; no se apunta en
     ningún archivo del proyecto.
   - *Windows Service*: dejar el servicio **MySQL84** marcado para que arranque con Windows, con *Standard
     System Account*.
   - *Server File Permissions*: primera opción (solo el servicio y los administradores acceden a la carpeta de
     datos `C:\ProgramData\MySQL\MySQL Server 8.4\`).
   - *Sample Databases*: no hacen falta.
   - Pulsar **Execute** y esperar a que todos los pasos salgan en verde.

### Comprobar que funciona

```powershell
Get-Service MySQL84
```

Tiene que aparecer `Running` (en cmd: `sc query MySQL84` → `RUNNING`).

Resultado en el equipo de desarrollo (26/11/2026): MySQL Server 8.4.13 instalado, servicio `MySQL84` en marcha
con inicio automático, escuchando en el puerto 3306 y sin ninguna regla de firewall que lo abra a la red.

Para usar el comando `mysql` desde la terminal hay que añadir
`C:\Program Files\MySQL\MySQL Server 8.4\bin` a la variable de entorno `Path` y abrir una terminal nueva:

```powershell
mysql --version
```

## 3. Crear la base de datos

El script está en [`docs/schema.sql`](../schema.sql) y está comentado línea a línea. Hace tres cosas:

1. Crea la base de datos `meetroll` con `utf8mb4`, para que se guarden bien las tildes y la ñ.
2. Crea el usuario `meetroll`, que es con el que se conectará el servidor. Solo tiene permisos `SELECT`, `INSERT`,
   `UPDATE` y `DELETE` sobre esa base de datos. Así, si alguien consiguiera los datos de conexión del servidor, no
   podría borrar tablas ni entrar en otras bases de datos.
3. Crea las cuatro tablas del modelo relacional de la memoria (apartado 8).

**La contraseña del usuario `meetroll`:** en el script pone `'CAMBIAR'`. Hay que sustituirla por la contraseña
elegida **en el editor, sin guardar el archivo**, para que no acabe subida a GitHub. Se usará otra vez en el
paso 2, en un archivo de configuración que tampoco se sube.

### Opción A: con Workbench

1. Abrir la conexión *Local instance MySQL84* con la contraseña de root.
2. *File → Open SQL Script* → `docs/schema.sql`.
3. Cambiar `'CAMBIAR'` por la contraseña del usuario `meetroll`.
4. Ejecutar todo el script con el icono del rayo.
5. Cerrar la pestaña **sin guardar**.

### Opción B: con la consola (la que se ha usado)

No hace falta editar el script: se ejecuta tal cual (el usuario `meetroll` se crea con la contraseña provisional
`CAMBIAR`) y después se cambia con `ALTER USER` dentro de MySQL. Así la contraseña real no queda escrita en
ningún archivo. Tampoco hace falta tocar el `Path`: se llama a `mysql.exe` con su ruta completa.

Desde el Símbolo del sistema (cmd):

```
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p
```

Pide la contraseña de root. Ya dentro (`mysql>`):

```sql
SOURCE C:/Users/1706s/Desktop/Meet-Roll/docs/schema.sql
ALTER USER 'meetroll'@'localhost' IDENTIFIED BY 'contraseña_elegida';
SHOW TABLES;
SHOW GRANTS FOR 'meetroll'@'localhost';
exit
```

- `SOURCE` ejecuta un archivo `.sql`. La ruta va con barras `/`; con `\` MySQL las interpreta como caracteres
  especiales.
- `ALTER USER` cambia la contraseña provisional por la definitiva.
- `SHOW GRANTS` muestra los permisos del usuario: solo `SELECT, INSERT, UPDATE, DELETE` sobre `meetroll.*`.

Por último, comprobar que el usuario de la aplicación puede entrar:

```
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u meetroll -p meetroll
```

El segundo `meetroll` es la base de datos que se abre al entrar. Dentro, `SHOW TABLES;` tiene que mostrar las
cuatro tablas.

### Comprobar el resultado

Conectado como root, en Workbench o en la consola (`mysql -u root -p`):

```sql
USE meetroll;
SHOW TABLES;
```

Tienen que salir las cuatro tablas: `evento`, `inscripcion`, `mensaje` y `usuario`.

## Resultado

Hecho el 26/11/2026 con **Workbench 26.10** (al final no se usó la consola).

1. En Workbench 26 la conexión no aparece sola: se crea con el **+** de *Database Connections* (Caption
   `MySQL84 root`, método `mysql`, usuario `root`, host `localhost`, puerto `3306`) sin pulsar *Store Password*,
   para que la contraseña se pida cada vez. *Test Connection* → «Connection test succeeded».
2. Se pegó el contenido de `schema.sql` en el editor, se seleccionó todo (Ctrl+A) y se ejecutó con **Run**:
   «8 statements executed successfully» (CREATE DATABASE, CREATE USER, GRANT, USE y los cuatro CREATE TABLE).
3. Con el editor vacío, se cambió la contraseña provisional:
   `ALTER USER 'meetroll'@'localhost' IDENTIFIED BY '...';`
4. Comprobación:
   - `SHOW TABLES FROM meetroll;` → `evento`, `inscripcion`, `mensaje`, `usuario`.
   - `SHOW GRANTS FOR 'meetroll'@'localhost';` → `GRANT USAGE ON *.*` (significa «ningún permiso global») y
     `GRANT SELECT, INSERT, UPDATE, DELETE ON meetroll.*`.

El archivo `docs/schema.sql` del repositorio no se modificó: sigue con `'CAMBIAR'` y no contiene ninguna
contraseña real.

**Buena práctica aprendida:** las contraseñas no se escriben en capturas, en el chat ni en archivos que se suben a
GitHub. Si una contraseña se ha enseñado, se cambia.

## Problemas conocidos

| Problema | Causa y solución |
|---|---|
| `mysql` no se reconoce como comando | Falta la carpeta `bin` en el `Path`, o la terminal se abrió antes de añadirla |
| `mysql.exe` se abre y se cierra al instante con doble clic | Es un programa de consola: hay que ejecutarlo desde cmd con `-u root -p`, o usar el acceso directo *MySQL 8.4 Command Line Client* |
| En Workbench, **Run** solo ejecuta una sentencia | Seleccionar todo el script (Ctrl+A) antes de pulsar **Run** |
| `ERROR 1007: database exists` o `ERROR 1050: table already exists` | El script ya se ejecutó. Para empezar de cero: `DROP DATABASE meetroll;` y volver a ejecutarlo |
| Un tutorial dice que se use `mysql_native_password` | En MySQL 8.4 ese método viene desactivado. El conector de Java usa el método por defecto (`caching_sha2_password`), así que no hay que cambiar nada |

## Archivos de este paso

| Archivo | Qué es |
|---|---|
| `docs/schema.sql` | Script que crea la base de datos, el usuario y las tablas |
| `.gitignore` | Archivos que Git no debe subir: las notas locales, la configuración con contraseñas y la carpeta `target/` de Maven |
