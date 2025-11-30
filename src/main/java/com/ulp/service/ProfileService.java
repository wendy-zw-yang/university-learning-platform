package com.ulp.service;

import com.ulp.bean.UserModel;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProfileService {
    private static final String UPLOAD_DIR = "/uploads/avatars/"; // Relative to app context

    public void updateProfile(UserModel user, String newPassword) {
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(newPassword);
        }
        // Mock: Update in DB
        System.out.println("Updated profile for: " + user.getUsername());
    }

    public String uploadAvatar(Part filePart, String username) throws IOException {
        String fileName = username + "_" + filePart.getSubmittedFileName();
        Path uploadPath = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(uploadPath.getParent());
        filePart.write(uploadPath.toString());
        return uploadPath.toString();
    }
}