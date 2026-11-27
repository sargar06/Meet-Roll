package com.meetroll.servidor.o3repository;

import com.meetroll.servidor.o3repository.entidades.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a la tabla MENSAJE. Las consultas del chat se añaden en el Sprint 3.
 */
public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {
}
