package com.ulp.servlet;

import com.ulp.bean.UserModel;
import com.ulp.service.AuthService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/register.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String email = req.getParameter("email");
        
        // 参数验证
        if (username == null || username.trim().isEmpty() ||
            password == null || password.isEmpty() ||
            email == null || email.trim().isEmpty()) {
            req.setAttribute("error", "所有字段都必须填写");
            req.setAttribute("username", username);
            req.setAttribute("email", email);
            doGet(req, resp);
            return;
        }
        
        // 验证密码确认
        if (confirmPassword != null && !password.equals(confirmPassword)) {
            req.setAttribute("error", "两次输入的密码不一致");
            req.setAttribute("username", username);
            req.setAttribute("email", email);
            doGet(req, resp);
            return;
        }
        
        // 密码强度验证（至少6位）
        if (password.length() < 6) {
            req.setAttribute("error", "密码长度至少为6位");
            req.setAttribute("username", username);
            req.setAttribute("email", email);
            doGet(req, resp);
            return;
        }

        UserModel user = new UserModel(username.trim(), password, "student", email.trim(), null);
        try {
            AuthService authService = new AuthService();
            authService.registerUser(user);
            req.getSession().setAttribute("successMessage", "注册成功，请登录");
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("username", username);
            req.setAttribute("email", email);
            doGet(req, resp);
        }
        RequestDispatcher dispatcher = req.getRequestDispatcher("/login.jsp");
    }
}