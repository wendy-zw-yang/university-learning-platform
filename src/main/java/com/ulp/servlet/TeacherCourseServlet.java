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

/**
 * 教师课程权限设置控制器 - 处理教师对其课程权限的修改
 */
@WebServlet("/teacher/courses")
public class TeacherCourseServlet extends HttpServlet {

    private CourseService courseService;
    private TeacherService teacherService;

    @Override
    public void init() throws ServletException {
        courseService = new CourseService();
        teacherService = new TeacherService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 检查用户是否登录且为教师
        HttpSession session = request.getSession(false);
        UserModel user = (UserModel) session.getAttribute("user");
        if (session == null || user == null || !"teacher".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            // 默认显示教师的课程列表
            showTeacherCourses(request, response, user.getId());
        } else if ("edit".equals(action)) {
            // 显示编辑页面
            showEditPermissionPage(request, response, user.getId());
        } else {
            showTeacherCourses(request, response, user.getId());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 检查用户是否登录且为教师
        HttpSession session = request.getSession(false);
        UserModel user = (UserModel) session.getAttribute("user");
        if (session == null || user == null || !"teacher".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("update".equals(action)) {
            updateCoursePermission(request, response, user.getId());
        } else {
            response.sendRedirect(request.getContextPath() + "/teacher/courses");
        }
    }

    /**
     * 显示教师的课程列表
     */
    private void showTeacherCourses(HttpServletRequest request, HttpServletResponse response, int teacherId)
            throws ServletException, IOException {

        List<CourseModel> courses = teacherService.getCoursesByTeacherId(teacherId);
        request.setAttribute("courses", courses);
        request.getRequestDispatcher("/teacher_courses.jsp").forward(request, response);
    }

    /**
     * 显示编辑课程权限页面
     */
    private void showEditPermissionPage(HttpServletRequest request, HttpServletResponse response, int teacherId)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                CourseModel course = courseService.getCourseById(id);

                // 验证该课程是否属于当前教师
                if (course != null && teacherId == course.getTeacherId()) {
                    request.setAttribute("course", course);
                    request.getRequestDispatcher("/teacher_edit_course.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "课程不存在或您没有权限修改此课程");
                    showTeacherCourses(request, response, teacherId);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "无效的课程ID");
                showTeacherCourses(request, response, teacherId);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/teacher/courses");
        }
    }

    /**
     * 更新课程权限
     */
    private void updateCoursePermission(HttpServletRequest request, HttpServletResponse response, int teacherId)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        String visibility = request.getParameter("visibility");

        if (idStr != null && !idStr.isEmpty() && visibility != null && !visibility.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                CourseModel existingCourse = courseService.getCourseById(id);

                // 验证该课程是否属于当前教师
                if (existingCourse != null && teacherId == existingCourse.getTeacherId()) {
                    // 更新课程权限
                    existingCourse.setVisibility(visibility);
                    boolean success = courseService.updateCourse(existingCourse);

                    if (success) {
                        response.sendRedirect(request.getContextPath() + "/teacher/courses?success=update");
                    } else {
                        request.setAttribute("error", "更新课程权限失败");
                        request.setAttribute("course", existingCourse);
                        request.getRequestDispatcher("/teacher_edit_course.jsp").forward(request, response);
                    }
                } else {
                    request.setAttribute("error", "您没有权限修改此课程");
                    showTeacherCourses(request, response, teacherId);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "无效的课程ID");
                showTeacherCourses(request, response, teacherId);
            }
        } else {
            request.setAttribute("error", "课程ID和可见性不能为空");
            showTeacherCourses(request, response, teacherId);
        }
    }
}
