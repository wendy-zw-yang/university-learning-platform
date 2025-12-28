package com.ulp.servlet;

import com.ulp.bean.*;
import com.ulp.dao.CourseDao;
import com.ulp.dao.impl.CourseDaoImpl;
import com.ulp.dao.impl.QuestionDaoImpl;
import com.ulp.dao.impl.AnswerDaoImpl;
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
    private List<CourseWithQuestionCount> getCoursesByTeacherId(int teacherId) {
        List<CourseWithQuestionCount> courses = new ArrayList<>();
        
        String sql = "SELECT c.*, u.username as teacher_name FROM courses c " +
                     "LEFT JOIN users u ON c.teacher_id = u.id " +
                     "WHERE c.teacher_id = ? OR c.id IN (" +
                     "SELECT tc.course_id FROM teacher_courses tc WHERE tc.teacher_id = ?" +
                     ") ORDER BY c.name";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teacherId);
            pstmt.setInt(2, teacherId);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CourseModel course = new CourseModel();
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setTeacherId(rs.getInt("teacher_id"));
                course.setDescription(rs.getString("description"));
                course.setCollege(rs.getString("college"));
                course.setVisibility(rs.getString("visibility"));
                course.setCreatedAt(rs.getTimestamp("created_at"));
                
                String teacherName = rs.getString("teacher_name");
                
                // 计算该课程的未回答问题数量
                int questionCount = getUnansweredQuestionCountByCourseId(course.getId());
                
                courses.add(new CourseWithQuestionCount(course, teacherName, questionCount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return courses;
    }
    
    // 获取指定课程的问题列表
    private List<QuestionWithAnswers> getQuestionsByCourseId(int courseId) {
        List<QuestionWithAnswers> questions = new ArrayList<>();
        
        String sql = "SELECT q.*, u.username as student_name FROM questions q " +
                     "LEFT JOIN users u ON q.student_id = u.id " +
                     "WHERE q.course_id = ? ORDER BY q.created_at DESC";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                QuestionModel question = new QuestionModel();
                question.setId(rs.getInt("id"));
                question.setTitle(rs.getString("title"));
                question.setContent(rs.getString("content"));
                question.setAttachment(rs.getString("attachment"));
                question.setCourseId(rs.getInt("course_id"));
                question.setStudentId(rs.getInt("student_id"));
                question.setCreatedAt(rs.getTimestamp("created_at"));
                
                String studentName = rs.getString("student_name");
                
                // 获取该问题的所有回答
                List<AnswerModel> answers = getAnswersByQuestionId(question.getId());
                
                questions.add(new QuestionWithAnswers(question, studentName, answers));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return questions;
    }
    
    // 获取指定问题的回答列表
    private List<AnswerModel> getAnswersByQuestionId(int questionId) {
        List<AnswerModel> answerList = new ArrayList<>();
        
        String sql = "SELECT a.*, u.username as teacher_name FROM answers a " +
                     "LEFT JOIN users u ON a.teacher_id = u.id " +
                     "WHERE a.question_id = ? ORDER BY a.created_at ASC";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                AnswerModel answer = new AnswerModel();
                answer.setId(rs.getInt("id"));
                answer.setContent(rs.getString("content"));
                answer.setAttachment(rs.getString("attachment"));
                answer.setQuestionId(rs.getInt("question_id"));
                answer.setTeacherId(rs.getInt("teacher_id"));
                answer.setCreatedAt(rs.getTimestamp("created_at"));
                answer.setTeacherName(rs.getString("teacher_name"));
                answerList.add(answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return answerList;
    }
    
    // 获取指定课程的未回答问题数量
    private int getUnansweredQuestionCountByCourseId(int courseId) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM questions WHERE course_id = ? AND id NOT IN (" +
                     "SELECT DISTINCT question_id FROM answers WHERE question_id IN (" +
                     "SELECT id FROM questions WHERE course_id = ?))";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return count;
    }
    
    // 获取指定课程的总问题数量
    private int getQuestionCountByCourseId(int courseId) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM questions WHERE course_id = ?";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return count;
    }
}