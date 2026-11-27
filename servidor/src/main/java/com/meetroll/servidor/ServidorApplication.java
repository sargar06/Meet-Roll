package com.meetroll.servidor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del servidor de Meet&Roll.
 *
 * @SpringBootApplication hace que Spring busque, dentro de este paquete y sus subpaquetes,
 * las clases marcadas como @Entity, @Repository, @Service o @RestController y las cree él solo.
 * También lee application.properties, abre la conexión con MySQL y arranca Tomcat en el puerto 8080.
 */
@SpringBootApplication
public class ServidorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServidorApplication.class, args);
    }
}
