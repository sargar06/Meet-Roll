package com.meetroll.servidor.o0dto;

/**
 * Datos que envía el cliente para iniciar sesión (CU-02).
 *
 * Ejemplo de JSON: { "email": "ana@correo.es", "password": "secreto123" }
 */
public record LoginDTO(String email, String password) {
}
