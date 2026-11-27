package com.meetroll.servidor.o0dto;

import com.meetroll.servidor.o3repository.entidades.Usuario;
import java.time.LocalDateTime;

/**
 * Datos de un usuario que el servidor devuelve a los clientes.
 * No incluye la contraseña: nunca sale del servidor, ni siquiera cifrada.
 */
public record UsuarioDTO(
        Integer id,
        String nombre,
        String email,
        String fotoUrl,
        String bio,
        boolean organizador,
        LocalDateTime fechaRegistro) {

    /**
     * Constructor de conversión: crea el DTO a partir de la entidad.
     * Igual que EmpleadoDTO(Empleados e) en el ejemplo de clase.
     */
    public UsuarioDTO(Usuario u) {
        this(u.getId(),
                u.getNombre(),
                u.getEmail(),
                u.getFotoUrl(),
                u.getBio(),
                u.isOrganizador(),
                u.getFechaRegistro());
    }
}
