package com.ulp.servlet;

import com.ulp.bean.UserModel;
import com.ulp.service.ProfileService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;

@WebServlet("/profile")
@MultipartConfig // For file upload
public class ProfileServlet extends HttpServlet {
    private ProfileService profileService = new ProfileService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("/login");
            return;
        }
        UserModel user = (UserModel) session.getAttribute("user");
        req.setAttribute("user", user);
        RequestDispatcher dispatcher = req.getRequestDispatcher("/profile.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("/login");
            return;
        }
        UserModel user = (UserModel) session.getAttribute("user");

        String newPassword = req.getParameter("newPassword");
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(newPassword);
        }
        String email = req.getParameter("email");
        user.setEmail(email);

//        Part avatarPart = req.getPart("avatar");
//        if (avatarPart != null && avatarPart.getSize() > 0) {
//            String avatarPath = profileService.uploadAvatar(avatarPart, user.getUsername());
//            user.setAvatar(avatarPath);
//        }

        profileService.updateProfile(user, newPassword);
        session.setAttribute("user", user); // Update session
        req.setAttribute("success", "Profile updated");
        // 重定向到dashboard servlet，由其根据角色进行分发
        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }
}