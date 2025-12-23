package com.ulp.servlet;

import com.ulp.bean.CourseModel;
import com.ulp.service.CourseService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * 课程控制器 - 处理课程相关的HTTP请求
 */
@WebServlet("/admin/courses")
public class CourseServlet extends HttpServlet {
    
    private CourseService courseService;

    @Override
    public void init() throws ServletException {
        courseService = new CourseService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 检查用户是否登录且为管理员
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        
        if (action == null) {
            // 默认显示课程列表
            showCourseList(request, response);
        } else if ("edit".equals(action)) {
            // 显示编辑页面
            showEditPage(request, response);
        } else if ("add".equals(action)) {
            // 显示添加页面
            request.getRequestDispatcher("/edit_course.jsp").forward(request, response);
        } else {
            showCourseList(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // 检查用户是否登录且为管理员
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            addCourse(request, response);
        } else if ("update".equals(action)) {
            updateCourse(request, response);
        } else if ("delete".equals(action)) {
            deleteCourse(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/courses");
        }
    }

    /**
     * 显示课程列表
     */
    private void showCourseList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<CourseModel> courses = courseService.getAllCourses();
        request.setAttribute("courses", courses);
        request.getRequestDispatcher("/courses.jsp").forward(request, response);
    }

    /**
     * 显示编辑页面
     */
    private void showEditPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                CourseModel course = courseService.getCourseById(id);
                
                if (course != null) {
                    request.setAttribute("course", course);
                    request.getRequestDispatcher("/edit_course.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "课程不存在");
                    showCourseList(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "无效的课程ID");
                showCourseList(request, response);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/courses");
        }
    }

    /**
     * 添加课程
     */
    private void addCourse(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String name = request.getParameter("name");
        String teacherIdStr = request.getParameter("teacherId");
        String description = request.getParameter("description");
        String college = request.getParameter("college");
        String visibility = request.getParameter("visibility");
        
        // 验证输入
        if (name == null || name.trim().isEmpty()) {
            request.setAttribute("error", "课程名称不能为空");
            request.getRequestDispatcher("/edit_course.jsp").forward(request, response);
            return;
        }
        
        CourseModel course = new CourseModel();
        course.setName(name.trim());
        
        if (teacherIdStr != null && !teacherIdStr.isEmpty()) {
            try {
                course.setTeacherId(Integer.parseInt(teacherIdStr));
            } catch (NumberFormatException e) {
                course.setTeacherId(null);
            }
        }
        
        course.setDescription(description != null ? description.trim() : "");
        course.setCollege(college != null ? college.trim() : "");
        course.setVisibility(visibility != null ? visibility : "all");
        
        boolean success = courseService.addCourse(course);
        
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/courses?success=add");
        } else {
            request.setAttribute("error", "添加课程失败");
            request.setAttribute("course", course);
            request.getRequestDispatcher("/edit_course.jsp").forward(request, response);
        }
    }

    /**
     * 更新课程
     */
    private void updateCourse(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String teacherIdStr = request.getParameter("teacherId");
        String description = request.getParameter("description");
        String college = request.getParameter("college");
        String visibility = request.getParameter("visibility");
        
        // 验证输入
        if (idStr == null || idStr.isEmpty() || name == null || name.trim().isEmpty()) {
            request.setAttribute("error", "课程ID和名称不能为空");
            request.getRequestDispatcher("/edit_course.jsp").forward(request, response);
            return;
        }
        
        try {
            CourseModel course = new CourseModel();
            course.setId(Integer.parseInt(idStr));
            course.setName(name.trim());
            
            if (teacherIdStr != null && !teacherIdStr.isEmpty()) {
                try {
                    course.setTeacherId(Integer.parseInt(teacherIdStr));
                } catch (NumberFormatException e) {
                    course.setTeacherId(null);
                }
            }
            
            course.setDescription(description != null ? description.trim() : "");
            course.setCollege(college != null ? college.trim() : "");
            course.setVisibility(visibility != null ? visibility : "all");
            
            boolean success = courseService.updateCourse(course);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/courses?success=update");
            } else {
                request.setAttribute("error", "更新课程失败");
                request.setAttribute("course", course);
                request.getRequestDispatcher("/edit_course.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "无效的课程ID");
            request.getRequestDispatcher("/edit_course.jsp").forward(request, response);
        }
    }

    /**
     * 删除课程
     */
    private void deleteCourse(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                boolean success = courseService.deleteCourse(id);
                
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/courses?success=delete");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/courses?error=delete");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/admin/courses?error=invalid");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/courses");
        }
    }
}
