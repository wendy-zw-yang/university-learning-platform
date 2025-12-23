package com.ulp.servlet;

import com.ulp.bean.Resource;
import com.ulp.bean.UserModel;
import com.ulp.service.ResourceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * 管理员资源管理Servlet
 */
@WebServlet("/admin/resource")
public class AdminResourceServlet extends HttpServlet {
    private ResourceService resourceService;

    @Override
    public void init() throws ServletException {
        resourceService = new ResourceService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查管理员权限
        if (!checkAdminPermission(req, resp)) {
            return;
        }

        String action = req.getParameter("action");
        
        if (action == null) {
            // 默认显示所有资源
            showAllResources(req, resp);
        } else {
            switch (action) {
                case "list":
                    showAllResources(req, resp);
                    break;
                case "edit":
                    showEditForm(req, resp);
                    break;
                case "delete":
                    deleteResource(req, resp);
                    break;
                default:
                    showAllResources(req, resp);
                    break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查管理员权限
        if (!checkAdminPermission(req, resp)) {
            return;
        }

        String action = req.getParameter("action");
        
        if ("update".equals(action)) {
            updateResource(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/resource");
        }
    }

    /**
     * 检查管理员权限
     */
    private boolean checkAdminPermission(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }

        UserModel user = (UserModel) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("<script>alert('无权限访问！'); window.location.href='" + 
                req.getContextPath() + "/dashboard.jsp';</script>");
            return false;
        }
        return true;
    }

    /**
     * 显示所有资源
     */
    private void showAllResources(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Resource> resources = resourceService.getAllResources();
        req.setAttribute("resources", resources);
        req.getRequestDispatcher("/admin-resource.jsp").forward(req, resp);
    }

    /**
     * 显示编辑表单
     */
    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/resource");
            return;
        }

        try {
            Integer id = Integer.parseInt(idStr);
            Resource resource = resourceService.getResourceById(id);
            
            if (resource == null) {
                req.setAttribute("error", "资源不存在！");
                showAllResources(req, resp);
                return;
            }

            req.setAttribute("editResource", resource);
            List<Resource> resources = resourceService.getAllResources();
            req.setAttribute("resources", resources);
            req.getRequestDispatcher("/admin-resource.jsp").forward(req, resp);
            
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/resource");
        }
    }

    /**
     * 删除资源
     */
    private void deleteResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/resource");
            return;
        }

        try {
            Integer id = Integer.parseInt(idStr);
            boolean success = resourceService.deleteResource(id);
            
            if (success) {
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().println("<script>alert('资源删除成功！'); window.location.href='" + 
                    req.getContextPath() + "/admin/resource';</script>");
            } else {
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().println("<script>alert('资源删除失败！'); window.location.href='" + 
                    req.getContextPath() + "/admin/resource';</script>");
            }
            
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/resource");
        }
    }

    /**
     * 更新资源信息
     */
    private void updateResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Integer id = Integer.parseInt(req.getParameter("id"));
            String title = req.getParameter("title");
            String description = req.getParameter("description");
            
            // 获取原资源信息
            Resource resource = resourceService.getResourceById(id);
            if (resource == null) {
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().println("<script>alert('资源不存在！'); window.location.href='" + 
                    req.getContextPath() + "/admin/resource';</script>");
                return;
            }

            // 更新资源信息
            resource.setTitle(title);
            resource.setDescription(description);
            
            boolean success = resourceService.updateResource(resource);
            
            if (success) {
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().println("<script>alert('资源更新成功！'); window.location.href='" + 
                    req.getContextPath() + "/admin/resource';</script>");
            } else {
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().println("<script>alert('资源更新失败！'); window.location.href='" + 
                    req.getContextPath() + "/admin/resource';</script>");
            }
            
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/resource");
        }
    }
}
