package com.ulp.servlet;

import com.ulp.bean.CourseModel;
import com.ulp.bean.UserModel;
import com.ulp.service.CourseService;
import com.ulp.service.TeacherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/teacher/edit_resources")
public class TeacherEditResourceServlet extends HttpServlet {
    private CourseService courseService;
    private TeacherService teacherService;

    public TeacherEditResourceServlet() {
        this.courseService = new CourseService();
        this.teacherService = new TeacherService();
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
            if (!"teacher".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String courseIdParam = request.getParameter("courseId");
        if (courseIdParam == null || courseIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/teacher/resource");
            return;
        }

        try {
            int courseId = Integer.parseInt(courseIdParam);
            UserModel user = (UserModel) userObj;

            // 验证教师是否有权限访问该课程
            List<com.ulp.bean.CourseModel> teacherCourses = teacherService.getCoursesByTeacherId(user.getId());
            boolean hasPermission = false;
            for (com.ulp.bean.CourseModel course : teacherCourses) {
                if (course.getId() == courseId) {
                    hasPermission = true;
                    break;
                }
            }

            if (!hasPermission) {
                request.setAttribute("error", "您没有权限访问此课程！");
                request.getRequestDispatcher("/teacher_upload_resource.jsp").forward(request, response);
                return;
            }

            // 获取课程信息
            CourseModel course = courseService.getCourseById(courseId);
            if (course != null) {
                request.setAttribute("course", course);
                request.getRequestDispatcher("/teacher_edit_resources.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/teacher/resource");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/teacher/resource");
        }
    }
}
