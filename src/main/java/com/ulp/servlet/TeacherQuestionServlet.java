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

@WebServlet("/teacher/questions")
public class TeacherQuestionServlet extends HttpServlet {
    // 用于封装课程和问题数量的内部类
    public static class CourseWithQuestionCount {
        private CourseModel course;
        private String teacherName;
        private int questionCount;
        
        public CourseWithQuestionCount(CourseModel course, String teacherName, int questionCount) {
            this.course = course;
            this.teacherName = teacherName;
            this.questionCount = questionCount;
        }
        
        public CourseModel getCourse() { return course; }
        public void setCourse(CourseModel course) { this.course = course; }
        
        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
        
        public int getQuestionCount() { return questionCount; }
        public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
    }
    
    // 用于封装问题和回答的内部类
    public static class QuestionWithAnswers {
        private QuestionModel question;
        private String studentName;
        private List<AnswerModel> answers;
        
        public QuestionWithAnswers(QuestionModel question, String studentName, List<AnswerModel> answers) {
            this.question = question;
            this.studentName = studentName;
            this.answers = answers;
        }
        
        public QuestionModel getQuestion() { return question; }
        public void setQuestion(QuestionModel question) { this.question = question; }
        
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        
        public List<AnswerModel> getAnswers() { return answers; }
        public void setAnswers(List<AnswerModel> answers) { this.answers = answers; }
        
        public boolean hasAnswers() { return answers != null && !answers.isEmpty(); }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 获取当前登录教师，这里假设已登录教师user存储在session中
        UserModel teacher = (UserModel) request.getSession().getAttribute("user");
        if (teacher == null || !"teacher".equals(teacher.getRole())) {
            // 如果未登录或不是教师，重定向到登录页面
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // 获取教师教授的课程列表
            List<CourseWithQuestionCount> courses = getCoursesByTeacherId(teacher.getId());
            
            // 获取课程ID参数
            String courseIdParam = request.getParameter("courseId");
            
            if (courseIdParam != null && !courseIdParam.isEmpty()) {
                try {
                    int courseId = Integer.parseInt(courseIdParam);
                    
                    // 验证该课程是否属于当前教师
                    boolean isTeacherCourse = false;
                    for (CourseWithQuestionCount course : courses) {
                        if (course.getCourse().getId() == courseId) {
                            isTeacherCourse = true;
                            break;
                        }
                    }
                    
                    if (!isTeacherCourse) {
                        request.setAttribute("error", "您没有权限查看此课程的问题");
                        request.getRequestDispatcher("/course_question.jsp").forward(request, response);
                        return;
                    }
                    
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

    // 根据教师ID获取课程列表
    public List<CourseWithQuestionCount> getCoursesByTeacherId(int teacherId) {
        QuestionService questionService = new QuestionService();
        List<CourseWithQuestionCount> genericCourses = questionService.getCoursesByTeacherId(teacherId);
        
        // 转换通用类到内部类
        List<CourseWithQuestionCount> teacherCourses = new ArrayList<>();
        for (CourseWithQuestionCount genericCourse : genericCourses) {
            teacherCourses.add(new CourseWithQuestionCount(
                genericCourse.getCourse(),
                genericCourse.getTeacherName(),
                genericCourse.getQuestionCount()
            ));
        }
        return teacherCourses;
    }
    
    // 获取指定课程的问题列表
    public List<QuestionWithAnswers> getQuestionsByCourseId(int courseId) {
        QuestionService questionService = new QuestionService();
        List<com.ulp.bean.QuestionWithAnswers> genericQuestions = questionService.getQuestionsByCourseIdForStudent(courseId);
        
        // 转换通用类到内部类
        List<QuestionWithAnswers> teacherQuestions = new ArrayList<>();
        for (com.ulp.bean.QuestionWithAnswers genericQuestion : genericQuestions) {
            teacherQuestions.add(new QuestionWithAnswers(
                genericQuestion.getQuestion(),
                genericQuestion.getStudentName(),
                genericQuestion.getAnswers()
            ));
        }
        return teacherQuestions;
    }
    
    // 获取指定问题的回答列表
    private List<AnswerModel> getAnswersByQuestionId(int questionId) {
        QuestionService  questionService = new QuestionService();
        return  questionService.getAnswersByQuestionId(questionId);
    }
    
    // 获取指定课程的未回答问题数量
    public int getUnansweredQuestionCountByCourseId(int courseId) {
        QuestionService  questionService = new QuestionService();
        return  questionService.getUnansweredQuestionCountByCourseId(courseId);
    }
    
    // 获取指定课程的总问题数量
    private int getQuestionCountByCourseId(int courseId) {
        QuestionService questionService = new QuestionService();
        return questionService.getQuestionCountByCourseId(courseId);
    }
}