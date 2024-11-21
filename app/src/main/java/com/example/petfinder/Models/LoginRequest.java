package com.example.petfinder.Models;

public class LoginRequest {
    private String email;
    private String contrase_a;

    public LoginRequest(String email, String contrase_a) {
        this.email = email;
        this.contrase_a = contrase_a;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrase_a() {
        return contrase_a;
    }

    public void setContrase_a(String contrase_a) {
        this.contrase_a = contrase_a;
    }
}
