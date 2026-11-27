package com.meetroll.servidor.o1controller;

import com.meetroll.servidor.o0dto.ErrorDTO;
import com.meetroll.servidor.o0dto.RegistroDTO;
import com.meetroll.servidor.o0dto.UsuarioDTO;
import com.meetroll.servidor.o2service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Recibe las peticiones de registro e inicio de sesión.
 *
 * @RestController: cada método responde a una petición HTTP y lo que devuelve se envía al cliente en JSON.
 * @RequestMapping: todas las rutas de esta clase empiezan por /api/auth.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * CU-01. POST http://localhost:8080/api/auth/registro
     *
     * @RequestBody: Spring convierte el JSON recibido en un RegistroDTO.
     * ResponseEntity permite elegir el código HTTP de la respuesta:
     *  - 201 Created: usuario creado (devuelve sus datos)
     *  - 400 Bad Request: datos no válidos
     *  - 409 Conflict: el correo ya existe
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroDTO datos) {
        try {
            UsuarioDTO creado = usuarioService.registrar(datos);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO(e.getMessage()));
        }
    }
}
