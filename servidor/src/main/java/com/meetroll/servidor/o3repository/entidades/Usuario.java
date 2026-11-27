package com.meetroll.servidor.o3repository.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Entidad que representa una fila de la tabla USUARIO.
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT de MySQL
    @Column(name = "id")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    // Se guarda la contraseña cifrada, nunca la original.
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "es_organizador", nullable = false)
    private boolean organizador;

    // Se rellena con la fecha y hora actuales al crear el objeto.
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    // JPA necesita un constructor vacío para crear los objetos al leer de la base de datos.
    public Usuario() {
    }

    public Usuario(String nombre, String email, String passwordHash, boolean organizador) {
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
        this.organizador = organizador;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isOrganizador() {
        return organizador;
    }

    public void setOrganizador(boolean organizador) {
        this.organizador = organizador;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return id + " - " + nombre + " (" + email + ")";
    }
}
