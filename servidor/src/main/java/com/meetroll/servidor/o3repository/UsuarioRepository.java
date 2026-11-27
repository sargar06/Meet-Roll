package com.meetroll.servidor.o3repository;

import com.meetroll.servidor.o3repository.entidades.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a la tabla USUARIO.
 *
 * Al heredar de JpaRepository<Usuario, Integer> (entidad, tipo del id), Spring genera
 * automáticamente save, findById, findAll, deleteById, count...
 * Es lo mismo que hacían los JpaController de clase, pero sin escribir el código.
 *
 * Los métodos que siguen la forma findBy + Atributo también los genera Spring
 * a partir del nombre: findByEmail hace "SELECT ... FROM usuario WHERE email = ?".
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Para el inicio de sesión. Optional porque puede que no exista ningún usuario con ese correo.
    Optional<Usuario> findByEmail(String email);

    // Para el registro: comprobar si el correo ya está usado.
    boolean existsByEmail(String email);
}
