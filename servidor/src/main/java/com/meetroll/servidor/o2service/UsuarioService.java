package com.meetroll.servidor.o2service;

import com.meetroll.servidor.o0dto.LoginDTO;
import com.meetroll.servidor.o0dto.PerfilDTO;
import com.meetroll.servidor.o0dto.RegistroDTO;
import com.meetroll.servidor.o0dto.UsuarioDTO;
import com.meetroll.servidor.o3repository.UsuarioRepository;
import com.meetroll.servidor.o3repository.entidades.Usuario;
import java.util.Optional;
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

    /**
     * CU-02. Comprueba el correo y la contraseña.
     *
     * @return los datos del usuario si son correctos, o null si no lo son.
     *         No se distingue entre "el correo no existe" y "la contraseña está mal",
     *         para no dar pistas de qué correos están registrados.
     */
    public UsuarioDTO comprobarCredenciales(LoginDTO datos) {
        if (datos.email() == null || datos.password() == null) {
            return null;
        }

        Optional<Usuario> encontrado = usuarioRepository.findByEmail(datos.email().trim().toLowerCase());
        if (encontrado.isEmpty()) {
            return null;
        }

        Usuario usuario = encontrado.get();
        // matches cifra la contraseña escrita y la compara con la guardada.
        if (!cifrador.matches(datos.password(), usuario.getPasswordHash())) {
            return null;
        }
        return new UsuarioDTO(usuario);
    }

    /**
     * CU-04. Devuelve los datos de un usuario, o null si no existe.
     */
    public UsuarioDTO obtener(Integer id) {
        Optional<Usuario> encontrado = usuarioRepository.findById(id);
        if (encontrado.isEmpty()) {
            return null;
        }
        return new UsuarioDTO(encontrado.get());
    }

    /**
     * CU-04. Cambia el nombre, la foto y la biografía de un usuario.
     *
     * @return el usuario actualizado, o null si no existe
     * @throws IllegalArgumentException si los datos no son válidos
     */
    public UsuarioDTO actualizarPerfil(Integer id, PerfilDTO datos) {
        if (datos.nombre() == null || datos.nombre().isBlank() || datos.nombre().length() > 100) {
            throw new IllegalArgumentException("El nombre es obligatorio y tiene como máximo 100 caracteres");
        }
        if (datos.fotoUrl() != null && datos.fotoUrl().length() > 255) {
            throw new IllegalArgumentException("La dirección de la foto tiene como máximo 255 caracteres");
        }
        if (datos.bio() != null && datos.bio().length() > 500) {
            throw new IllegalArgumentException("La biografía tiene como máximo 500 caracteres");
        }

        Optional<Usuario> encontrado = usuarioRepository.findById(id);
        if (encontrado.isEmpty()) {
            return null;
        }

        Usuario usuario = encontrado.get();
        usuario.setNombre(datos.nombre().trim());
        usuario.setFotoUrl(datos.fotoUrl());
        usuario.setBio(datos.bio());

        // save con un usuario que ya tiene id hace un UPDATE en vez de un INSERT.
        return new UsuarioDTO(usuarioRepository.save(usuario));
    }
}
