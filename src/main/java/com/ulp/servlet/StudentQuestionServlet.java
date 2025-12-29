package com.ulp.servlet;

import com.ulp.bean.*;
import com.ulp.dao.CourseDao;
import com.ulp.dao.impl.CourseDaoImpl;
import com.ulp.dao.impl.QuestionDaoImpl;
import com.ulp.dao.impl.AnswerDaoImpl;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/student/questions")
public class StudentQuestionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 获取当前登录学生，这里假设已登录学生user存储在session中
        UserModel student= (UserModel) request.getSession().getAttribute("user");
        if (student == null) {
            // 如果未登录，重定向到登录页面
            System.out.println("未登录!@ StudentQuestionServlet");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // 获取课程列表
            List<CourseWithQuestionCount> courses = getCoursesByStudentId(student.getId());
            
            // 获取课程ID参数
            String courseIdParam = request.getParameter("courseId");
            
            if (courseIdParam != null && !courseIdParam.isEmpty()) {
                try {
                    int courseId = Integer.parseInt(courseIdParam);
                    
                    // 获取该课程的问题列表
                    List<QuestionWithAnswers> questions = getQuestionsByCourseId(courseId);
                    
                    // 设置请求属性
                    request.setAttribute("questions", questions);
                    request.setAttribute("selectedCourseId", courseId);
                    
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "课程ID格式不正确");
                }
            }
            
            // 设置课程列表
            request.setAttribute("courses", courses);
            
            // 转发到课程-问题页面
            request.getRequestDispatcher("/course_question.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "系统错误：" + e.getMessage());
            request.getRequestDispatcher("/course_question.jsp").forward(request, response);
        }
    }

    // 根据学生ID获取课程列表
    private List<CourseWithQuestionCount> getCoursesByStudentId(int studentId) {
        QuestionService questionService = new QuestionService();
        List<com.ulp.bean.CourseWithQuestionCount> genericCourses = questionService.getCoursesByStudentId(studentId);
        
        // 转换通用类到内部类
        List<CourseWithQuestionCount> studentCourses = new ArrayList<>();
        for (com.ulp.bean.CourseWithQuestionCount genericCourse : genericCourses) {
            studentCourses.add(new CourseWithQuestionCount(
                genericCourse.getCourse(),
                genericCourse.getTeacherName(),
                genericCourse.getQuestionCount()
            ));
        }
        return studentCourses;
    }
    
    // 获取学生已选课程ID列表
    private List<Integer> getEnrolledCourseIds(int studentId) {
        QuestionService questionService = new QuestionService();
        return questionService.getEnrolledCourseIds(studentId);
    }
    
    // 获取指定课程的问题列表
    private List<QuestionWithAnswers> getQuestionsByCourseId(int courseId) {
        QuestionService questionService = new QuestionService();
        List<com.ulp.bean.QuestionWithAnswers> genericQuestions = questionService.getQuestionsByCourseIdForStudent(courseId);
        
        // 转换通用类到内部类
        List<QuestionWithAnswers> studentQuestions = new ArrayList<>();
        for (com.ulp.bean.QuestionWithAnswers genericQuestion : genericQuestions) {
            studentQuestions.add(new QuestionWithAnswers(
                genericQuestion.getQuestion(),
                genericQuestion.getStudentName(),
                genericQuestion.getAnswers()
            ));
        }
        return studentQuestions;
    }
    
    // 获取指定问题的回答列表
    private List<AnswerModel> getAnswersByQuestionId(int questionId) {
        QuestionService questionService = new QuestionService();
        return questionService.getAnswersByQuestionId(questionId);
    }
    
    // 获取指定课程的问题数量
    private int getQuestionCountByCourseId(int courseId) {
        QuestionService questionService = new QuestionService();
        return questionService.getQuestionCountByCourseId(courseId);
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