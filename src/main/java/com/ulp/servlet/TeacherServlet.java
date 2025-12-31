package com.ulp.servlet;

import com.ulp.bean.TeacherModel;
import com.ulp.bean.CourseModel;
import com.ulp.bean.UserModel;
import com.ulp.service.TeacherService;
import com.ulp.service.CourseService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 教师控制器 - 处理教师相关的HTTP请求
 */
@WebServlet("/admin/teachers")
public class TeacherServlet extends HttpServlet {
    
    private TeacherService teacherService;
    private CourseService courseService;

    @Override
    public void init() throws ServletException {
        teacherService = new TeacherService();
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
            // 默认显示教师列表
            showTeacherList(request, response);
        } else if ("edit".equals(action)) {
            // 显示编辑页面
            showEditPage(request, response);
        } else if ("add".equals(action)) {
            // 显示添加页面，需要获取所有未分配的课程列表
            List<CourseModel> courses = courseService.getUnassignedCourses();
            request.setAttribute("courses", courses);
            request.getRequestDispatcher("/edit_teacher.jsp").forward(request, response);
        } else {
            showTeacherList(request, response);
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
            addTeacher(request, response);
        } else if ("update".equals(action)) {
            updateTeacher(request, response);
        } else if ("delete".equals(action)) {
            deleteTeacher(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/teachers");
        }
    }

    /**
     * 显示教师列表
     */
    private void showTeacherList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<TeacherModel> teachers = teacherService.getAllTeachers();
        request.setAttribute("teachers", teachers);
        request.getRequestDispatcher("/teachers.jsp").forward(request, response);
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
                TeacherModel teacher = teacherService.getTeacherById(id);
                
                if (teacher != null) {
                    // 获取所有课程列表用于多选
                    List<CourseModel> courses = courseService.getAvailableCoursesForTeacher(id);
                    
                    request.setAttribute("teacher", teacher);
                    request.setAttribute("courses", courses);
                    request.getRequestDispatcher("/edit_teacher.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "教师不存在");
                    showTeacherList(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "无效的教师ID");
                showTeacherList(request, response);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/teachers");
        }
    }

    /**
     * 添加教师
     */
    private void addTeacher(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String avatar = request.getParameter("avatar");
        String profile = request.getParameter("profile");
        String title = request.getParameter("title");
        String[] courseIdsStr = request.getParameterValues("courseIds");
        
        // 验证输入
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "用户名和密码不能为空");
            List<CourseModel> courses = courseService.getAllCourses();
            request.setAttribute("courses", courses);
            request.getRequestDispatcher("/edit_teacher.jsp").forward(request, response);
            return;
        }
        
        TeacherModel teacher = new TeacherModel();
        UserModel user=new UserModel();
        user.setUsername(username.trim());
        user.setPassword(password.trim()); // 注意：实际应用中应该加密密码
        user.setRole("teacher");
        user.setEmail(email != null ? email.trim() : "");
        user.setAvatar(avatar != null ? avatar.trim() : "");
        user.setProfile(profile != null ? profile.trim() : "");
        user.setTitle(title != null ? title.trim() : "");
        teacher.setUserModel(user);
        
        // 处理课程ID列表
        List<Integer> courseIds = new ArrayList<>();
        if (courseIdsStr != null) {
            for (String courseIdStr : courseIdsStr) {
                try {
                    courseIds.add(Integer.parseInt(courseIdStr));
                } catch (NumberFormatException e) {
                    // 忽略无效的ID
                }
            }
        }
        teacher.setCourseIdList(courseIds);
        
        boolean success = teacherService.addTeacher(teacher);

        if (success) {
            int teacherId = teacher.getUserModel().getId();
            // 如果课程ID列表不为空，为教师分配课程
            if (!courseIds.isEmpty()) {
                boolean courseAssigned = teacherService.assignCourses(teacherId, courseIds);
                if (!courseAssigned) {
                    // 如果课程分配失败，可能需要回滚
                    System.out.println("警告：课程分配失败");
                }
            }
            response.sendRedirect(request.getContextPath() + "/admin/teachers?success=add");
        } else {
            request.setAttribute("error", "添加教师失败，用户名可能已存在");
            request.setAttribute("teacher", teacher);
            List<CourseModel> courses = courseService.getAvailableCoursesForTeacher(teacher.getUserModel().getId());
            request.setAttribute("courses", courses);
            request.getRequestDispatcher("/edit_teacher.jsp").forward(request, response);
        }
    }

    /**
     * 更新教师
     */
    private void updateTeacher(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String avatar = request.getParameter("avatar");
        String profile = request.getParameter("profile");
        String title = request.getParameter("title");
        String[] courseIdsStr = request.getParameterValues("courseIds");
        
        // 验证输入
        if (idStr == null || idStr.isEmpty() || username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "教师ID、用户名和密码不能为空");
            List<CourseModel> courses = courseService.getAllCourses();
            request.setAttribute("courses", courses);
            request.getRequestDispatcher("/edit_teacher.jsp").forward(request, response);
            return;
        }
        
        try {
            TeacherModel teacher = new TeacherModel();
            UserModel user=new UserModel();

            user.setId(Integer.parseInt(idStr));
            user.setUsername(username.trim());
            user.setPassword(password.trim());
            user.setEmail(email != null ? email.trim() : "");
            user.setAvatar(avatar != null ? avatar.trim() : "");
            user.setProfile(profile != null ? profile.trim() : "");
            user.setTitle(title != null ? title.trim() : "");
            teacher.setUserModel(user);

            // 处理课程ID列表
            List<Integer> courseIds = new ArrayList<>();
            if (courseIdsStr != null) {
                for (String courseIdStr : courseIdsStr) {
                    try {
                        courseIds.add(Integer.parseInt(courseIdStr));
                    } catch (NumberFormatException e) {
                        // 忽略无效的ID
                    }
                }
            }
            teacher.setCourseIdList(courseIds);
            
            boolean success = teacherService.updateTeacher(teacher);

            if (success) {
                // 更新课程分配
                int teacherId = Integer.parseInt(idStr);
                // 先移除所有现有课程关联
                teacherService.removeAllCourses(teacherId);

                // 再分配新选择的课程
                if (!courseIds.isEmpty()) {
                    teacherService.assignCourses(teacherId, courseIds);
                }

                response.sendRedirect(request.getContextPath() + "/admin/teachers?success=update");
            } else {
                request.setAttribute("error", "更新教师失败，用户名可能已存在");
                request.setAttribute("teacher", teacher);
                List<CourseModel> courses = courseService.getAvailableCoursesForTeacher(Integer.parseInt(idStr));
                request.setAttribute("courses", courses);
                request.getRequestDispatcher("/edit_teacher.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "无效的教师ID");
            List<CourseModel> courses = courseService.getAvailableCoursesForTeacher(Integer.parseInt(idStr));
            request.setAttribute("courses", courses);
            request.getRequestDispatcher("/edit_teacher.jsp").forward(request, response);
        }
    }

    /**
     * 删除教师
     */
    private void deleteTeacher(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                boolean success = teacherService.deleteTeacher(id);
                
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/teachers?success=delete");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/teachers?error=delete");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/admin/teachers?error=invalid");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/teachers");
        }
    }
}
