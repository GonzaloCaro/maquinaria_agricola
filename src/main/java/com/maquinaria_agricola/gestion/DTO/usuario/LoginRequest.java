package com.maquinaria_agricola.gestion.DTO.usuario;

public class LoginRequest {
    private String userName;
    private String password;

    // getters/setters

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}