package com.ulp.servlet;

import com.ulp.bean.CourseModel;
import com.ulp.bean.UserModel;
import com.ulp.service.CourseService;
import com.ulp.service.StudentCourseService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/student/edit_resources")
public class StudentEditResourceServlet extends HttpServlet {
    private CourseService courseService;
    private StudentCourseService studentCourseService;

    public StudentEditResourceServlet() {
        this.courseService = new CourseService();
        this.studentCourseService = new StudentCourseService();
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
            if (!"student".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String courseIdParam = request.getParameter("courseId");
        if (courseIdParam == null || courseIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/student/resource");
            return;
        }

        try {
            int courseId = Integer.parseInt(courseIdParam);
            UserModel user = (UserModel) userObj;

            // 获取课程信息
            CourseModel course = courseService.getCourseById(courseId);
            if (course != null) {
                // 获取学生可见的课程列表
                List<CourseModel> allCoursesList = courseService.getAllCourses();
                List<Integer> enrolledCourseIdsList = studentCourseService.getEnrolledCourseIds(user.getId());
                List<CourseModel> visibleCourses = getVisibleCourses(allCoursesList, enrolledCourseIdsList);

                request.setAttribute("courses", visibleCourses);
                request.setAttribute("course", course); // 传递课程信息
                request.setAttribute("selectedCourseId", courseId);
                request.getRequestDispatcher("/student_edit_resource.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/student/resource");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/student/resource");
        }
    }

    private List<CourseModel> getVisibleCourses(List<CourseModel> allCourses, List<Integer> enrolledCourseIds) {
        List<CourseModel> visibleCourses = new java.util.ArrayList<>();
        for (CourseModel course : allCourses) {
            // 如果课程权限为"all"或学生已选该课程，则该课程对当前学生可见
            if ("all".equals(course.getVisibility()) || enrolledCourseIds.contains(course.getId())) {
                visibleCourses.add(course);
            }
        }
        return visibleCourses;
    }
}
