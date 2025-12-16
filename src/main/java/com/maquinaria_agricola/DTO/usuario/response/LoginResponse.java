package com.maquinaria_agricola.DTO.usuario.response;

import java.util.UUID;

public class LoginResponse {
    private String accessToken;
    private UUID userId;
    private String userName;
    private String role;

    public LoginResponse(String accessToken, UUID userId, String userName, String role) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.userName = userName;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getRole() {
        return role;
    }

}