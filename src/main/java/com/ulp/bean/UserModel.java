package com.ulp.bean;

import java.sql.Timestamp;

public class UserModel {
    private String id;
    private String username;
    private String password;
    private String role="student"; // "admin", "student", "teacher"
    private String email;
    private String avatar; // 头像路径
    private String profile; // 简介，仅教师用
    private String title; // 职称，仅教师用
    private Timestamp createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // Constructors
    public UserModel() {}

    public UserModel(String username, String password, String role, String email, String avatar) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.avatar = avatar;
    }

    // Getters and Setters
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

    // Validation method
    public boolean validate() {
        boolean valid= username != null && !username.isEmpty() &&
                password != null && !password.isEmpty() &&
                role != null && (role.equals("admin") || role.equals("student") || role.equals("teacher"));
        if(email!= null && !email.isEmpty())
            valid = valid && email.matches("^[\\w-_.+]*[\\w-_.]@([\\w]+[.])+[\\w]+$"); // Basic email regex
        return valid;
    }
}