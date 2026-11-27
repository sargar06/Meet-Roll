package com.meetroll.servidor.o1controller;

import com.meetroll.servidor.o0dto.ErrorDTO;
import com.meetroll.servidor.o0dto.LoginDTO;
import com.meetroll.servidor.o0dto.RegistroDTO;
import com.meetroll.servidor.o0dto.SesionDTO;
import com.meetroll.servidor.o0dto.UsuarioDTO;
import com.meetroll.servidor.o2service.SesionService;
import com.meetroll.servidor.o2service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Recibe las peticiones de registro, inicio y cierre de sesión.
 *
 * @RestController: cada método responde a una petición HTTP y lo que devuelve se envía al cliente en JSON.
 * @RequestMapping: todas las rutas de esta clase empiezan por /api/auth.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final SesionService sesionService;

    public AuthController(UsuarioService usuarioService, SesionService sesionService) {
        this.usuarioService = usuarioService;
        this.sesionService = sesionService;
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

    /**
     * CU-02. POST http://localhost:8080/api/auth/login
     *
     *  - 200 OK: devuelve el token y los datos del usuario
     *  - 401 Unauthorized: correo o contraseña incorrectos (mismo mensaje en los dos casos)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO datos) {
        UsuarioDTO usuario = usuarioService.comprobarCredenciales(datos);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorDTO("Correo o contraseña incorrectos"));
        }
        String token = sesionService.crearSesion(usuario.id());
        return ResponseEntity.ok(new SesionDTO(token, usuario));
    }

    /**
     * Cerrar sesión. POST http://localhost:8080/api/auth/logout
     * con la cabecera "Authorization: Bearer <token>". Responde 204 (sin contenido).
     *
     * @RequestHeader: lee una cabecera de la petición. required = false porque puede no venir.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String cabecera) {
        sesionService.cerrarSesion(cabecera);
        return ResponseEntity.noContent().build();
    }
}
