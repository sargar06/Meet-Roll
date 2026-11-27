package com.meetroll.servidor.o3repository;

import com.meetroll.servidor.o3repository.entidades.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a la tabla EVENTO. Las consultas de filtros del mapa se añaden en el Sprint 2.
 */
public interface EventoRepository extends JpaRepository<Evento, Integer> {
}
