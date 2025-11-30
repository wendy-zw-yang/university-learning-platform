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
    private AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/login.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (authService.authenticate(username, password)) {
            HttpSession session = req.getSession();
            UserModel user = new UserModel(username, password, "student", "example@email.com", null); // Mock user
            session.setAttribute("user", user);
            resp.sendRedirect("/dashboard.jsp"); // Assume dashboard exists
        } else {
            req.setAttribute("error", "Invalid credentials");
            doGet(req, resp); // Forward back to login with error
        }
    }
}