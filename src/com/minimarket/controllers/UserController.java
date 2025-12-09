package com.minimarket.controllers;

import com.minimarket.services.UserService;
import com.minimarket.models.User;
import java.util.List;

public class UserController {
    private UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public boolean addUser(User user) {
        validateUser(user, true); // True = Cek password (wajib ada saat create)
        return userService.addUser(user);
    }

    public boolean updateUser(User user) {
        validateUser(user, false); // False = Password opsional (kalau kosong berarti tidak diubah)
        return userService.updateUser(user);
    }

    public boolean deleteUser(int id) {
        // Mencegah hapus diri sendiri (sedang login)
        User currentUser = LoginController.getCurrentUser();
        if (currentUser != null && currentUser.getId() == id) {
            throw new IllegalArgumentException("Tidak dapat menghapus akun yang sedang digunakan!");
        }
        return userService.deleteUser(id);
    }

    private void validateUser(User user, boolean isNew) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama harus diisi!");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email harus diisi!");
        }
        if (isNew) {
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Password wajib diisi untuk user baru!");
            }
        }
    }
}