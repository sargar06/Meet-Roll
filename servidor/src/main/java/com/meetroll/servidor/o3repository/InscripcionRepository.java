package com.meetroll.servidor.o3repository;

import com.meetroll.servidor.o3repository.entidades.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a la tabla INSCRIPCION. Se usa en el Sprint 2 (unirse a un evento).
 */
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
}
