package com.ulp.servlet;

import com.ulp.bean.ResourceModel;
import com.ulp.service.ResourceService;
import com.ulp.service.CourseService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/resource")
public class AdminResourceServlet extends HttpServlet {
    private ResourceService resourceService;
    private CourseService courseService;

    public AdminResourceServlet() {
        this.resourceService = new ResourceService();
        this.courseService = new CourseService();
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

        String action = request.getParameter("action");
        String resourceIdParam = request.getParameter("id");
        String courseIdParam = request.getParameter("courseId");

        if ("edit".equals(action) && resourceIdParam != null) {
            // 编辑资源 - 显示编辑表单
            try {
                Integer resourceId = Integer.parseInt(resourceIdParam);
                ResourceModel resource = resourceService.getResourceById(resourceId);
                if (resource != null) {
                    // 获取所有课程用于编辑页面
                    List<com.ulp.bean.CourseModel> courses = courseService.getAllCourses();
                    request.setAttribute("courses", courses);
                    request.setAttribute("resource", resource);
                    request.getRequestDispatcher("/admin_edit_resource.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "无效的资源ID！");
            }
        }

        // 获取所有资源和课程
        List<ResourceModel> resources = resourceService.getAllResources();
        List<com.ulp.bean.CourseModel> courses = courseService.getAllCourses();

        request.setAttribute("resources", resources);
        request.setAttribute("courses", courses);

        request.getRequestDispatcher("/admin-resource.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            // 删除资源
            String idParam = request.getParameter("id");
            if (idParam != null) {
                try {
                    Integer id = Integer.parseInt(idParam);
                    boolean success = resourceService.deleteResource(id);
                    if (success) {
                        request.setAttribute("success", "资源删除成功！");
                    } else {
                        request.setAttribute("error", "删除资源失败！");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "无效的资源ID！");
                }
            } else {
                request.setAttribute("error", "无效的资源ID！");
            }
        } else if ("update".equals(action)) {
            // 更新资源信息
            String idParam = request.getParameter("id");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String courseIdParam = request.getParameter("courseId");

            if (idParam != null && title != null && !title.trim().isEmpty()) {
                try {
                    Integer id = Integer.parseInt(idParam);
                    Integer courseId = null;
                    if (courseIdParam != null && !courseIdParam.isEmpty()) {
                        courseId = Integer.parseInt(courseIdParam);
                    }

                    ResourceModel resource = resourceService.getResourceById(id);
                    if (resource != null) {
                        resource.setTitle(title);
                        resource.setDescription(description);
                        if (courseId != null) {
                            resource.setCourseId(courseId);
                        }
                        boolean success = resourceService.updateResource(resource);
                        if (success) {
                            request.setAttribute("success", "资源信息更新成功！");
                        } else {
                            request.setAttribute("error", "更新资源信息失败！");
                        }
                    } else {
                        request.setAttribute("error", "找不到指定的资源！");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "无效的资源ID或课程ID！");
                }
            } else {
                request.setAttribute("error", "请输入完整的资源信息！");
            }
        }

        // 重定向到资源管理页面
        String courseIdParamInRequest = request.getParameter("courseId");
        String redirectUrl = request.getContextPath() + "/admin/resource";
        if (courseIdParamInRequest != null && !courseIdParamInRequest.isEmpty()) {
            redirectUrl += "?courseId=" + courseIdParamInRequest;
        }
        response.sendRedirect(redirectUrl);
    }
}
