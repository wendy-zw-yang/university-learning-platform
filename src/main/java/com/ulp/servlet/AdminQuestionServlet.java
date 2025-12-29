package com.ulp.servlet;

import com.ulp.bean.*;
import com.ulp.service.QuestionService;
import com.ulp.util.DBHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/questions")
public class AdminQuestionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 获取当前登录管理员，这里假设已登录管理员user存储在session中
        UserModel admin = (UserModel) request.getSession().getAttribute("user");
        if (admin == null || !"admin".equals(admin.getRole())) {
            // 如果未登录或不是管理员，重定向到登录页面
            System.out.println("未登录或无权限!@ AdminQuestionServlet");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // 获取课程列表
            QuestionService questionService = new QuestionService();
            List<com.ulp.bean.CourseWithQuestionCount> genericCourses = questionService.getAllCoursesWithQuestionCount();
            
            // 转换通用类到内部类
            List<CourseWithQuestionCount> adminCourses = new ArrayList<>();
            for (com.ulp.bean.CourseWithQuestionCount genericCourse : genericCourses) {
                adminCourses.add(new CourseWithQuestionCount(
                    genericCourse.getCourse(),
                    genericCourse.getTeacherName(),
                    genericCourse.getQuestionCount()
                ));
            }
            
            // 设置课程列表
            request.setAttribute("courses", adminCourses);
            
            // 获取课程ID参数
            String courseIdParam = request.getParameter("courseId");
            
            if (courseIdParam != null && !courseIdParam.isEmpty()) {
                try {
                    int courseId = Integer.parseInt(courseIdParam);
                    
                    // 获取该课程的问题列表
                    List<com.ulp.bean.QuestionWithAnswers> genericQuestions = questionService.getQuestionsByCourseIdForAdmin(courseId);
                    
                    // 转换通用类到内部类
                    List<QuestionWithAnswers> adminQuestions = new ArrayList<>();
                    for (com.ulp.bean.QuestionWithAnswers genericQuestion : genericQuestions) {
                        adminQuestions.add(new QuestionWithAnswers(
                            genericQuestion.getQuestion(),
                            genericQuestion.getStudentName(),
                            genericQuestion.getAnswers()
                        ));
                    }
                    
                    // 设置请求属性
                    request.setAttribute("questions", adminQuestions);
                    request.setAttribute("selectedCourseId", courseId);
                    
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "课程ID格式不正确");
                }
            }
            
            // 转发到课程-问题页面
            request.getRequestDispatcher("/admin_course_question.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "系统错误：" + e.getMessage());
            request.getRequestDispatcher("/admin_course_question.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 获取当前登录管理员
        UserModel admin = (UserModel) request.getSession().getAttribute("user");
        if (admin == null || !"admin".equals(admin.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        // 改进调试信息 - 输出所有参数
        System.out.println("AdminQuestionServlet doPost - action parameter: " + action);

        // 输出所有参数的详细信息
        java.util.Enumeration<String> paramNames = request.getParameterNames();
        System.out.println("All parameters:");
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            System.out.println("  " + paramName + " = " + paramValue);
        }

        // 处理删除操作
        if ("delete".equals(action)) {
            handleDelete(request, response);
        } else {
            // 这个分支不应该被执行，除非action参数有问题
            System.out.println("Invalid action parameter: " + action);
            // 修改处1: 添加else处理无效action，避免doPost结束无响应导致空白
            response.sendRedirect(request.getContextPath() + "/admin/questions?error=无效操作");
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String questionIdParam = request.getParameter("questionId");
        String answerIdParam = request.getParameter("answerId");
        String courseIdParam = request.getParameter("courseId");
        
        try {
            QuestionService questionService = new QuestionService();
            
            if (questionIdParam != null && !questionIdParam.isEmpty()) {
                // 删除问题（以及其相关回答）
                int questionId = Integer.parseInt(questionIdParam);
                boolean success = questionService.deleteQuestionById(questionId);
                
                if (success) {
                    if (courseIdParam != null && !courseIdParam.isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/admin/questions?courseId=" + courseIdParam + "&success=问题删除成功");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/questions?success=问题删除成功");
                    }
                } else {
                    if (courseIdParam != null && !courseIdParam.isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/admin/questions?courseId=" + courseIdParam + "&error=删除失败");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/questions?error=删除失败");
                    }
                }
            } else if (answerIdParam != null && !answerIdParam.isEmpty()) {
                // 删除回答
                int answerId = Integer.parseInt(answerIdParam);
                boolean success = questionService.deleteAnswerById(answerId);
                
                if (success) {
                    if (courseIdParam != null && !courseIdParam.isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/admin/questions?courseId=" + courseIdParam + "&success=回答删除成功");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/questions?success=回答删除成功");
                    }
                } else {
                    if (courseIdParam != null && !courseIdParam.isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/admin/questions?courseId=" + courseIdParam + "&error=删除失败");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/questions?error=删除失败");
                    }
                }
            }
        } catch (NumberFormatException e) {
            if (courseIdParam != null && !courseIdParam.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/admin/questions?courseId=" + courseIdParam + "&error=ID格式不正确");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/questions?error=ID格式不正确");
            }
        }
    }
    
    // 用于封装课程和问题数量的内部类
    public static class CourseWithQuestionCount extends com.ulp.bean.CourseWithQuestionCount {
        public CourseWithQuestionCount(CourseModel course, String teacherName, int questionCount) {
            super(course, teacherName, questionCount);
        }
    }
    
    // 用于封装问题和回答的内部类
    public static class QuestionWithAnswers extends com.ulp.bean.QuestionWithAnswers {
        public QuestionWithAnswers(QuestionModel question, String studentName, List<AnswerModel> answers) {
            super(question, studentName, answers);
        }
        
        public QuestionWithAnswers() { super(null, null, null); }
    }
}