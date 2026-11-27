package com.meetroll.servidor.o0dto;

/**
 * Datos que envía el cliente para registrarse (CU-01).
 *
 * Ejemplo de JSON:
 * { "nombre": "Ana", "email": "ana@correo.es", "password": "secreto123", "organizador": false }
 */
public record RegistroDTO(
        String nombre,
        String email,
        String password,
        boolean organizador) {
}
