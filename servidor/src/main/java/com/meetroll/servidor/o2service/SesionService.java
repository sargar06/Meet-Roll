package com.meetroll.servidor.o2service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * Guarda las sesiones abiertas: qué token corresponde a qué usuario.
 *
 * Al iniciar sesión se genera un token aleatorio (UUID) y se guarda en el mapa junto al id del usuario.
 * El cliente envía ese token en cada petición (cabecera "Authorization: Bearer <token>") y el servidor
 * lo busca aquí para saber quién es. El id del usuario nunca se toma de lo que envía el cliente.
 *
 * Las sesiones están en memoria: si el servidor se reinicia, hay que volver a iniciar sesión.
 */
@Service
public class SesionService {

    /*
     * Tomcat atiende cada petición en un hilo distinto, así que varios hilos pueden leer y escribir
     * el mapa a la vez. ConcurrentHashMap es un HashMap preparado para eso (sin tener que usar synchronized).
     */
    private final Map<String, Integer> sesiones = new ConcurrentHashMap<>();

    /**
     * Abre una sesión para el usuario y devuelve su token.
     */
    public String crearSesion(Integer idUsuario) {
        // UUID aleatorio, por ejemplo "3f1c9a2e-7b4d-4e8a-9c1f-2a6b8d0e4f17". Imposible de adivinar.
        String token = UUID.randomUUID().toString();
        sesiones.put(token, idUsuario);
        return token;
    }

    /**
     * Devuelve el id del usuario a partir de la cabecera Authorization, o null si no hay sesión válida.
     *
     * @param cabecera valor de la cabecera, con la forma "Bearer <token>" (puede ser null)
     */
    public Integer usuarioDeLaCabecera(String cabecera) {
        String token = extraerToken(cabecera);
        if (token == null) {
            return null;
        }
        return sesiones.get(token);
    }

    /**
     * Cierra la sesión: el token deja de valer.
     */
    public void cerrarSesion(String cabecera) {
        String token = extraerToken(cabecera);
        if (token != null) {
            sesiones.remove(token);
        }
    }

    // Quita el prefijo "Bearer " de la cabecera. Devuelve null si la cabecera no tiene esa forma.
    private String extraerToken(String cabecera) {
        if (cabecera == null || !cabecera.startsWith("Bearer ")) {
            return null;
        }
        return cabecera.substring("Bearer ".length()).trim();
    }
}
