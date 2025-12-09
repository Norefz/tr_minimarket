package com.minimarket.controllers;

import com.minimarket.services.AuthService;
import com.minimarket.models.User;

public class LoginController {
    private AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email harus diisi");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password harus diisi");
        }

        return authService.login(email, password);
    }

    public void logout() {
        authService.logout();
    }

    public static User getCurrentUser() {
        return AuthService.getCurrentUser();
    }

    public static boolean isLoggedIn() {
        return AuthService.isLoggedIn();
    }
}