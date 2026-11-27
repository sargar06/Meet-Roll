package com.meetroll.servidor.o0dto;

/**
 * Datos del perfil que el usuario puede cambiar (CU-04).
 * El correo y el rol de organizador no se cambian desde el perfil.
 *
 * Ejemplo de JSON: { "nombre": "Ana", "fotoUrl": null, "bio": "Juego a Catan y a Magic" }
 */
public record PerfilDTO(String nombre, String fotoUrl, String bio) {
}
