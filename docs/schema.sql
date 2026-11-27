-- =====================================================================
-- Meet&Roll - Base de datos (MySQL 8.4)
--
-- Crea la base de datos, un usuario para la aplicación y las 4 tablas.
-- Se ejecuta una sola vez, conectado como root.
--
-- ANTES DE EJECUTARLO: cambia 'CAMBIAR' (más abajo) por la contraseña
-- que quieras para el usuario meetroll. No subas esa contraseña a GitHub.
-- =====================================================================

-- 1. Base de datos.
--    utf8mb4 permite guardar cualquier carácter (tildes, ñ, emojis).
CREATE DATABASE IF NOT EXISTS meetroll
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

-- 2. Usuario de la aplicación.
--    El servidor no se conecta como root: usa este usuario, que solo puede
--    leer, insertar, modificar y borrar filas de la base meetroll.
--    No puede borrar tablas ni tocar otras bases de datos.
CREATE USER IF NOT EXISTS 'meetroll'@'localhost' IDENTIFIED BY 'CAMBIAR';
GRANT SELECT, INSERT, UPDATE, DELETE ON meetroll.* TO 'meetroll'@'localhost';

USE meetroll;

-- 3. Tablas.
--    Se crean en este orden porque cada tabla solo puede apuntar (FOREIGN KEY)
--    a tablas que ya existan.

-- Usuarios registrados. es_organizador indica si puede crear eventos.
CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,          -- nunca la contraseña en claro
    foto_url VARCHAR(255),
    bio VARCHAR(500),
    es_organizador BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Eventos del mapa. latitud y longitud con 6 decimales (precisión de ~10 cm).
CREATE TABLE evento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    organizador_id INT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    tipo ENUM('MANGA_COMIC','TCG','JUEGOS_MESA','WARGAMES','ROL','OTROS') NOT NULL,
    fecha_hora DATETIME NOT NULL,
    latitud DECIMAL(9,6) NOT NULL,
    longitud DECIMAL(9,6) NOT NULL,
    aforo INT,
    FOREIGN KEY (organizador_id) REFERENCES usuario(id)
);

-- Qué usuario se ha unido a qué evento.
-- UNIQUE impide que el mismo usuario se apunte dos veces al mismo evento.
CREATE TABLE inscripcion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    evento_id INT NOT NULL,
    fecha_inscripcion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (usuario_id, evento_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (evento_id) REFERENCES evento(id)
);

-- Mensajes de chat, privados y de grupo, en una sola tabla:
--   - privado: destinatario_id tiene valor y evento_id es NULL
--   - de grupo: evento_id tiene valor y destinatario_id es NULL
-- El CHECK obliga a que siempre se cumpla una de las dos (funciona desde MySQL 8.0.16).
-- No hay ON DELETE CASCADE porque MySQL no lo permite junto a este CHECK: al borrar
-- un evento, el servidor borra antes sus mensajes y sus inscripciones.
CREATE TABLE mensaje (
    id INT AUTO_INCREMENT PRIMARY KEY,
    remitente_id INT NOT NULL,
    destinatario_id INT NULL,
    evento_id INT NULL,
    contenido TEXT NOT NULL,
    fecha_envio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (remitente_id) REFERENCES usuario(id),
    FOREIGN KEY (destinatario_id) REFERENCES usuario(id),
    FOREIGN KEY (evento_id) REFERENCES evento(id),
    CONSTRAINT chk_mensaje_destino CHECK (
        (destinatario_id IS NOT NULL AND evento_id IS NULL)
        OR (destinatario_id IS NULL AND evento_id IS NOT NULL))
);
