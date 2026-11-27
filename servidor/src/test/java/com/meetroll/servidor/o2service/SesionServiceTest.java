package com.meetroll.servidor.o2service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del mapa de sesiones (inicio y cierre de sesión, CU-02).
 * No necesitan base de datos ni arrancar el servidor: SesionService se crea con new.
 */
class SesionServiceTest {

    private SesionService sesiones;

    // @BeforeEach: se ejecuta antes de cada test, así cada uno empieza con un mapa vacío.
    @BeforeEach
    void prepararSesiones() {
        sesiones = new SesionService();
    }

    @Test
    void conElTokenSeObtieneElUsuario() {
        String token = sesiones.crearSesion(7);

        assertEquals(7, sesiones.usuarioDeLaCabecera("Bearer " + token));
    }

    @Test
    void cadaSesionTieneUnTokenDistinto() {
        String token1 = sesiones.crearSesion(1);
        String token2 = sesiones.crearSesion(1);

        assertNotEquals(token1, token2);
    }

    @Test
    void sinCabeceraNoHayUsuario() {
        assertNull(sesiones.usuarioDeLaCabecera(null));
    }

    @Test
    void conUnTokenInventadoNoHayUsuario() {
        sesiones.crearSesion(1);

        assertNull(sesiones.usuarioDeLaCabecera("Bearer 1234-inventado"));
    }

    @Test
    void sinLaPalabraBearerNoHayUsuario() {
        String token = sesiones.crearSesion(1);

        // El token es correcto, pero la cabecera no tiene la forma "Bearer <token>".
        assertNull(sesiones.usuarioDeLaCabecera(token));
    }

    @Test
    void despuesDeCerrarSesionElTokenNoVale() {
        String token = sesiones.crearSesion(3);

        sesiones.cerrarSesion("Bearer " + token);

        assertNull(sesiones.usuarioDeLaCabecera("Bearer " + token));
    }
}
