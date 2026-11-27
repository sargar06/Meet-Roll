package com.meetroll.servidor.o1controller;

import com.meetroll.servidor.o0dto.ErrorDTO;
import com.meetroll.servidor.o0dto.PerfilDTO;
import com.meetroll.servidor.o0dto.UsuarioDTO;
import com.meetroll.servidor.o2service.SesionService;
import com.meetroll.servidor.o2service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CU-04. Ver y editar el perfil del usuario que ha iniciado sesión.
 *
 * El usuario se saca siempre del token (cabecera "Authorization: Bearer <token>"),
 * nunca de un id enviado por el cliente: así nadie puede ver ni cambiar el perfil de otro.
 */
@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    private final UsuarioService usuarioService;
    private final SesionService sesionService;

    public PerfilController(UsuarioService usuarioService, SesionService sesionService) {
        this.usuarioService = usuarioService;
        this.sesionService = sesionService;
    }

    /**
     * GET http://localhost:8080/api/perfil
     *  - 200 OK: datos del usuario
     *  - 401 Unauthorized: sin token o token no válido
     */
    @GetMapping
    public ResponseEntity<?> ver(@RequestHeader(value = "Authorization", required = false) String cabecera) {
        Integer idUsuario = sesionService.usuarioDeLaCabecera(cabecera);
        if (idUsuario == null) {
            return sinSesion();
        }

        UsuarioDTO usuario = usuarioService.obtener(idUsuario);
        if (usuario == null) {
            return sinSesion(); // el usuario se ha borrado mientras tenía la sesión abierta
        }
        return ResponseEntity.ok(usuario);
    }

    /**
     * PUT http://localhost:8080/api/perfil
     *  - 200 OK: perfil actualizado (devuelve los datos nuevos)
     *  - 400 Bad Request: datos no válidos
     *  - 401 Unauthorized: sin token o token no válido
     */
    @PutMapping
    public ResponseEntity<?> editar(@RequestHeader(value = "Authorization", required = false) String cabecera,
                                    @RequestBody PerfilDTO datos) {
        Integer idUsuario = sesionService.usuarioDeLaCabecera(cabecera);
        if (idUsuario == null) {
            return sinSesion();
        }

        try {
            UsuarioDTO actualizado = usuarioService.actualizarPerfil(idUsuario, datos);
            if (actualizado == null) {
                return sinSesion();
            }
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO(e.getMessage()));
        }
    }

    // Respuesta 401 común a los dos métodos.
    private ResponseEntity<ErrorDTO> sinSesion() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDTO("Hay que iniciar sesión"));
    }
}
