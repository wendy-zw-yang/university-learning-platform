package com.ulp.servlet;

import com.ulp.bean.*;
import com.ulp.dao.CourseDao;
import com.ulp.dao.impl.CourseDaoImpl;
import com.ulp.dao.impl.QuestionDaoImpl;
import com.ulp.dao.impl.AnswerDaoImpl;
import com.ulp.service.QuestionService;
import com.ulp.util.DBHelper;
import com.ulp.bean.QuestionWithAnswers;
import com.ulp.bean.CourseWithQuestionCount;

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
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        UserModel teacher = (UserModel) request.getSession().getAttribute("user");
        if (teacher == null || !"teacher".equals(teacher.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            handleDeleteAnswer(request, response, teacher.getId());
        } else if ("update".equals(action)) {
            handleUpdateAnswer(request, response, teacher.getId());
        } else if ("edit".equals(action)) {
            showEditAnswerPage(request, response);
        }
    }

    // 处理删除回答
    private void handleDeleteAnswer(HttpServletRequest request, HttpServletResponse response, int teacherId) 
            throws IOException {
        try {
            int answerId = Integer.parseInt(request.getParameter("answerId"));
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            
            // 验证该回答是否属于该教师
            QuestionService questionService = new QuestionService();
            QuestionDaoImpl questionDao = new QuestionDaoImpl();
            AnswerModel answer = questionDao.getAnswerById(answerId);
            if (answer != null && answer.getTeacherId() == teacherId) {
                // 删除回答
                boolean success = questionService.deleteAnswerById(answerId);
                
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/teacher/questions?courseId=" + courseId);
                } else {
                    response.sendRedirect(request.getContextPath() + "/teacher/questions?courseId=" + courseId);
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/teacher/questions?courseId=" + courseId);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/teacher/questions");
        }
    }

    // 处理更新回答
    private void handleUpdateAnswer(HttpServletRequest request, HttpServletResponse response, int teacherId) 
            throws IOException {
        try {
            int answerId = Integer.parseInt(request.getParameter("answerId"));
            String newContent = request.getParameter("newContent");
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            
            // 验证该回答是否属于该教师
            QuestionService questionService = new QuestionService();
            QuestionDaoImpl questionDao = new QuestionDaoImpl();
            AnswerModel answer = questionDao.getAnswerById(answerId);
            if (answer != null && answer.getTeacherId() == teacherId) {
                // 更新回答
                boolean success = questionService.updateAnswerContent(answerId, newContent);
                
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/teacher/questions?courseId=" + courseId);
                } else {
                    response.sendRedirect(request.getContextPath() + "/teacher/questions?courseId=" + courseId);
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/teacher/questions?courseId=" + courseId);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/teacher/questions");
        }
    }

    // 显示编辑回答页面
    private void showEditAnswerPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        UserModel teacher = (UserModel) request.getSession().getAttribute("user");
        if (teacher == null || !"teacher".equals(teacher.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int answerId = Integer.parseInt(request.getParameter("answerId"));
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            
            // 验证该回答是否属于该教师
            QuestionDaoImpl questionDao = new QuestionDaoImpl();
            AnswerModel answer = questionDao.getAnswerById(answerId);
            if (answer != null && answer.getTeacherId() == teacher.getId()) {
                request.setAttribute("answer", answer);
                request.setAttribute("courseId", courseId);
                request.getRequestDispatcher("/edit_answer.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "您没有权限编辑此回答");
                doGet(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "参数格式错误");
            doGet(request, response);
        }
    }
    
    // 根据教师ID获取课程列表
    public List<CourseWithQuestionCount> getCoursesByTeacherId(int teacherId) {
        QuestionService questionService = new QuestionService();
        List<com.ulp.bean.CourseWithQuestionCount> genericCourses = questionService.getCoursesByTeacherId(teacherId);
        
        // 转换通用类到内部类
        List<CourseWithQuestionCount> teacherCourses = new ArrayList<>();
        for (com.ulp.bean.CourseWithQuestionCount genericCourse : genericCourses) {
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