package com.meetroll.servidor.o2service;

import com.meetroll.servidor.o0dto.RegistroDTO;
import com.meetroll.servidor.o0dto.UsuarioDTO;
import com.meetroll.servidor.o3repository.UsuarioRepository;
import com.meetroll.servidor.o3repository.entidades.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Lógica de los usuarios: registro (CU-01), inicio de sesión (CU-02) y perfil (CU-04).
 *
 * Errores:
 *  - IllegalArgumentException -> los datos no son válidos (el controlador responde 400).
 *  - IllegalStateException    -> el correo ya está registrado (el controlador responde 409).
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // BCrypt cifra las contraseñas. El resultado no se puede descifrar.
    private final BCryptPasswordEncoder cifrador = new BCryptPasswordEncoder();

    /**
     * En clase el servicio creaba su repositorio con new.
     * Aquí Spring crea el UsuarioRepository y nos lo pasa por el constructor.
     */
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * CU-01. Registra un usuario nuevo.
     *
     * @param datos datos enviados por el cliente
     * @return el usuario guardado, ya con su id
     */
    public UsuarioDTO registrar(RegistroDTO datos) {
        // 1. Comprobar los datos
        if (datos.nombre() == null || datos.nombre().isBlank() || datos.nombre().length() > 100) {
            throw new IllegalArgumentException("El nombre es obligatorio y tiene como máximo 100 caracteres");
        }
        if (datos.email() == null || !datos.email().contains("@") || datos.email().length() > 150) {
            throw new IllegalArgumentException("El correo no es válido");
        }
        // BCrypt solo usa los primeros 72 caracteres, por eso el máximo.
        if (datos.password() == null || datos.password().length() < 8 || datos.password().length() > 72) {
            throw new IllegalArgumentException("La contraseña debe tener entre 8 y 72 caracteres");
        }

        // 2. El correo se guarda siempre en minúsculas y sin espacios,
        //    para que "Ana@Correo.es" y "ana@correo.es" sean la misma cuenta.
        String email = datos.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalStateException("Ya existe una cuenta con ese correo");
        }

        // 3. Crear la entidad con la contraseña cifrada y guardarla
        Usuario usuario = new Usuario(
                datos.nombre().trim(),
                email,
                cifrador.encode(datos.password()),
                datos.organizador());

        Usuario guardado = usuarioRepository.save(usuario);

        // 4. Devolver un DTO (sin la contraseña)
        return new UsuarioDTO(guardado);
    }
}
