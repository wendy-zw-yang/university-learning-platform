package com.ulp.servlet;

import com.ulp.bean.UserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/dashboard")
public class HomepageServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        
        // 检查用户是否登录
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        UserModel user = (UserModel) session.getAttribute("user");
        String role = user.getRole();
        
        // 根据用户角色重定向到相应的主页
        switch (role.toLowerCase()) {
            case "admin":
                resp.sendRedirect(req.getContextPath() + "/admin_homepage.jsp");
                break;
            case "teacher":
                resp.sendRedirect(req.getContextPath() + "/teacher_homepage.jsp");
                break;
            case "student":
                resp.sendRedirect(req.getContextPath() + "/student_homepage.jsp");
                break;
            default:
                // 如果角色未知，返回错误页面或登录页面
                req.setAttribute("error", "未知的用户角色");
                resp.sendRedirect(req.getContextPath() + "/login");
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
