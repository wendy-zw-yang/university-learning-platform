package com.ulp.service;

import com.ulp.bean.UserModel;
import com.ulp.dao.UserDao;
import com.ulp.dao.impl.UserDaoImpl;
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
        UserDao userDAO = new UserDaoImpl();
        userDAO.updateProfileInDatabase(user);

        System.out.println("Updated profile for: " + user.toString());
    }

    public String uploadAvatar(Part filePart, String username) throws IOException {
        String fileName = username + "_" + filePart.getSubmittedFileName();
        Path uploadPath = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(uploadPath.getParent());
        filePart.write(uploadPath.toString());
        return uploadPath.toString();
    }
}