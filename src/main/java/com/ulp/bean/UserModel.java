package com.ulp.bean;

import java.sql.Timestamp;

public class UserModel {
    private int id;
    private String username;
    private String password;
    private String role; // "admin", "student", "teacher"
    private String email;
    private String avatar; // File path or URL
    private Timestamp createdAt;

    // Constructors
    public UserModel() {}

    public UserModel(int id, String username, String password, String role, String email, String avatar) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.avatar = avatar;
    }
    
    public UserModel(String username, String password, String role, String email, String avatar) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.avatar = avatar;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // Validation method
    public boolean validate() {
        return username != null && !username.isEmpty() &&
                password != null && !password.isEmpty() &&
                role != null && (role.equals("admin") || role.equals("student") || role.equals("teacher")) &&
                email != null && email.matches("^[\\w-_.+]*[\\w-_.]@([\\w]+[.])+[\\w]+$"); // Basic email regex
    }
}