package com.ulp.servlet;

import com.ulp.bean.ResourceModel;
import com.ulp.service.ResourceService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin/resource/preview")
public class AdminResourcePreviewServlet extends HttpServlet {
    private ResourceService resourceService;

    public AdminResourcePreviewServlet() {
        this.resourceService = new ResourceService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 验证用户权限
        HttpSession session = request.getSession();
        Object userObj = session.getAttribute("user");

        if (userObj == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 检查用户角色
        try {
            String role = (String) userObj.getClass().getMethod("getRole").invoke(userObj);
            if (!"admin".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String resourceIdParam = request.getParameter("id");
        if (resourceIdParam == null || resourceIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/resource");
            return;
        }

        try {
            Integer resourceId = Integer.parseInt(resourceIdParam);
            ResourceModel resource = resourceService.getResourceById(resourceId);

            if (resource == null) {
                response.sendRedirect(request.getContextPath() + "/admin/resource");
                return;
            }

            request.setAttribute("resource", resource);
            request.getRequestDispatcher("/admin_resource_preview.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/resource");
        }
    }
}
