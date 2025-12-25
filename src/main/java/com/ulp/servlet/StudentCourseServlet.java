package com.ulp.servlet;

import com.ulp.bean.CourseModel;
import com.ulp.bean.UserModel;
import com.ulp.service.StudentCourseService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/student/courses")
public class StudentCourseServlet extends HttpServlet {

    private StudentCourseService studentCourseService;

    @Override
    public void init() throws ServletException {
        studentCourseService = new StudentCourseService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 检查用户是否登录且为学生
        HttpSession session = request.getSession(false);
        UserModel user = (UserModel) session.getAttribute("user");
        if (session == null || user == null || !"student".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 获取所有课程和学生已选课程
        List<CourseModel> allCourses = studentCourseService.getAllCourses();
        List<Integer> enrolledCourseIds = studentCourseService.getEnrolledCourseIds(user.getId());

        request.setAttribute("courses", allCourses);
        request.setAttribute("enrolledCourseIds", enrolledCourseIds);
        request.getRequestDispatcher("/student_courses.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 检查用户是否登录且为学生
        HttpSession session = request.getSession(false);
        UserModel user = (UserModel) session.getAttribute("user");
        if (session == null || user == null || !"student".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String courseIdStr = request.getParameter("courseId");

        if (courseIdStr != null && !courseIdStr.isEmpty()) {
            try {
                int courseId = Integer.parseInt(courseIdStr);

                if ("enroll".equals(action)) {
                    // 选课
                    boolean success = studentCourseService.enrollCourse(user.getId(), courseId);
                    if (success) {
                        response.sendRedirect(request.getContextPath() + "/student/courses?success=enroll");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/student/courses?error=enroll");
                    }
                } else if ("unenroll".equals(action)) {
                    // 退课
                    boolean success = studentCourseService.unenrollCourse(user.getId(), courseId);
                    if (success) {
                        response.sendRedirect(request.getContextPath() + "/student/courses?success=unenroll");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/student/courses?error=unenroll");
                    }
                } else {
                    response.sendRedirect(request.getContextPath() + "/student/courses");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/student/courses?error=invalid");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/student/courses");
        }
    }
}
