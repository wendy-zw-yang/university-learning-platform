package com.universitylearningplatform.service;

import com.universitylearningplatform.bean.UserModel;

public class AuthService {
    // Simulate database or storage (in-memory for now)
    // In real scenario, use DAO to interact with DB

    public boolean authenticate(String username, String password) {
        // Mock logic: Check against stored users
        // Example: if username == "test" && password == "pass", return true
        return "test".equals(username) && "pass".equals(password);
    }

    public void registerUser(UserModel user) {
        if (!user.validate()) {
            throw new IllegalArgumentException("Invalid user data");
        }
        // Mock: Save to DB (print for simulation)
        System.out.println("Registered user: " + user.getUsername());
    }
}