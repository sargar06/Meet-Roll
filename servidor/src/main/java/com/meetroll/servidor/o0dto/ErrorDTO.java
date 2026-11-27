package com.meetroll.servidor.o0dto;

/**
 * Respuesta cuando algo falla. Ejemplo de JSON: { "error": "Ya existe una cuenta con ese correo" }
 */
public record ErrorDTO(String error) {
}
