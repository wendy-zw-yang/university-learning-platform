package com.ulp.servlet;

import com.ulp.bean.UserModel;
import com.ulp.service.AuthService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login") // Annotation for mapping (complements web.xml)
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/login.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        AuthService authService = new AuthService();
        
        // 参数验证
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            req.setAttribute("error", "用户名和密码不能为空");
            doGet(req, resp);
            return;
        }

        // 认证用户
        UserModel user = authService.authenticate(username.trim(), password);
        if (user != null) {
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            // 重定向到dashboard servlet，由其根据角色进行分发
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            req.setAttribute("error", "用户名或密码错误");
            req.setAttribute("username", username); // 保留用户名
            doGet(req, resp);
        }
    }
}