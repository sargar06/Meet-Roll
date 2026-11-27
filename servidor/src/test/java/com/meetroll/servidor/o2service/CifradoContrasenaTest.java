package com.meetroll.servidor.o2service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Pruebas del cifrado de contraseñas con BCrypt, el mismo que usa UsuarioService.
 */
class CifradoContrasenaTest {

    private final BCryptPasswordEncoder cifrador = new BCryptPasswordEncoder();

    @Test
    void laContrasenaCifradaNoEsLaOriginal() {
        String cifrada = cifrador.encode("secreto123");

        assertNotEquals("secreto123", cifrada);
    }

    @Test
    void laContrasenaCorrectaCoincide() {
        String cifrada = cifrador.encode("secreto123");

        assertTrue(cifrador.matches("secreto123", cifrada));
    }

    @Test
    void unaContrasenaIncorrectaNoCoincide() {
        String cifrada = cifrador.encode("secreto123");

        assertFalse(cifrador.matches("otraclave1", cifrada));
    }

    @Test
    void cifrarDosVecesLaMismaContrasenaDaResultadosDistintos() {
        // BCrypt añade una "sal" aleatoria: dos usuarios con la misma contraseña no tienen el mismo hash.
        assertNotEquals(cifrador.encode("secreto123"), cifrador.encode("secreto123"));
    }
}
