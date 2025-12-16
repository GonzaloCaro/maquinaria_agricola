package com.maquinaria_agricola.DTO.usuario;

public class RegisterRequest {

    private String nombre;
    private String apellido;
    private String userName;
    private String email;
    private String contrasena;

    // Constructores
    public RegisterRequest() {
    }

    public RegisterRequest(String nombre, String apellido, String userName, String email, String contrasena) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.userName = userName;
        this.email = email;
        this.contrasena = contrasena;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
