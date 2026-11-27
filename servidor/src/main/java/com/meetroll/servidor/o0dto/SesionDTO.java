package com.meetroll.servidor.o0dto;

/**
 * Respuesta a un inicio de sesión correcto: el token que el cliente debe enviar
 * en las siguientes peticiones y los datos del usuario.
 *
 * Ejemplo de JSON: { "token": "3f1c9a2e-...", "usuario": { "id": 1, "nombre": "Ana", ... } }
 */
public record SesionDTO(String token, UsuarioDTO usuario) {
}
