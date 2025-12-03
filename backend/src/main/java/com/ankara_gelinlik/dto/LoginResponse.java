package com.ankara_gelinlik.dto;

public class LoginResponse {

    private String id;        // String olarak frontend uyumlu
    private String email;
    private String role;      // Enum.name() olarak string
    private String token;     // JWT access token

    public LoginResponse() {}

    public LoginResponse(String id, String email, String role, String token) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.token = token;
    }

    // --- getters & setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
