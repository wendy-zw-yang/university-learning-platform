package com.ulp.dao.impl;

import com.ulp.bean.AnswerModel;
import com.ulp.bean.CourseModel;
import com.ulp.bean.QuestionModel;
import com.ulp.dao.QuestionDao;
import com.ulp.bean.CourseWithQuestionCount;
import com.ulp.bean.QuestionWithAnswers;
import com.ulp.servlet.TeacherQuestionServlet;
import com.ulp.util.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDaoImpl implements QuestionDao {
    @Override
    // 保存问题到数据库
    public boolean saveQuestion(QuestionModel question) {
        String sql = "INSERT INTO questions (title, content, attachment, course_id, student_id, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, question.getTitle());
            pstmt.setString(2, question.getContent());
            pstmt.setString(3, question.getAttachment());
            pstmt.setInt(4, question.getCourseId());
            pstmt.setInt(5, question.getStudentId());
            pstmt.setTimestamp(6, question.getCreatedAt());

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    // 根据ID获取问题
    public QuestionModel getQuestionById(int id) {
        String sql = "SELECT * FROM questions WHERE id = ?";
        QuestionModel question = null;
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                question = new QuestionModel();
                question.setId(rs.getInt("id"));
                question.setTitle(rs.getString("title"));
                question.setContent(rs.getString("content"));
                question.setAttachment(rs.getString("attachment"));
                question.setCourseId(rs.getInt("course_id"));
                question.setStudentId(rs.getInt("student_id"));
                question.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return question;
    }

    @Override
    public List<TeacherQuestionServlet.CourseWithQuestionCount> getCoursesByTeacherId(int teacherId) {
        List<com.ulp.bean.CourseWithQuestionCount> genericCourses = getCoursesByTeacherIdGeneric(teacherId);
        
        // 转换通用类到教师端内部类
        List<TeacherQuestionServlet.CourseWithQuestionCount> teacherCourses = new ArrayList<>();
        for (com.ulp.bean.CourseWithQuestionCount genericCourse : genericCourses) {
            teacherCourses.add(new TeacherQuestionServlet.CourseWithQuestionCount(
                genericCourse.getCourse(),
                genericCourse.getTeacherName(),
                genericCourse.getQuestionCount()
            ));
        }
        return teacherCourses;
    }
    
    @Override
    public List<com.ulp.bean.CourseWithQuestionCount> getCoursesByTeacherIdGeneric(int teacherId) {
        List<com.ulp.bean.CourseWithQuestionCount> courses = new ArrayList<>();

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

                courses.add(new com.ulp.bean.CourseWithQuestionCount(course, teacherName, questionCount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }

    // 获取指定课程的问题列表
    @Override
    public List<TeacherQuestionServlet.QuestionWithAnswers> getQuestionsByCourseId(int courseId) {
        List<TeacherQuestionServlet.QuestionWithAnswers> questions = new ArrayList<>();

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

                questions.add(new TeacherQuestionServlet.QuestionWithAnswers(question, studentName, answers));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }

    // 获取指定问题的回答列表
    @Override
    public List<AnswerModel> getAnswersByQuestionId(int questionId) {
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
    @Override
    public int getUnansweredQuestionCountByCourseId(int courseId) {
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
    @Override
    public int getQuestionCountByCourseId(int courseId) {
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

    public List<com.ulp.bean.CourseWithQuestionCount> getCoursesByStudentId(int studentId) {
        List<com.ulp.bean.CourseWithQuestionCount> courses = new ArrayList<>();

        // 获取学生已选课程ID列表
        List<Integer> enrolledCourseIds = getEnrolledCourseIds(studentId);

        if (enrolledCourseIds.isEmpty()) {
            return courses; // 返回空列表
        }

        // 构建IN子句的参数
        String inSql = String.join(",", enrolledCourseIds.stream().map(String::valueOf).toArray(String[]::new));
        String sql = "SELECT c.*, u.username as teacher_name FROM courses c " +
                "LEFT JOIN users u ON c.teacher_id = u.id WHERE c.id IN (" + inSql + ")";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

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

                // 计算该课程的问题数量
                int questionCount = getQuestionCountByCourseId(course.getId());

                courses.add(new com.ulp.bean.CourseWithQuestionCount(course, teacherName, questionCount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }

    // 获取学生已选课程ID列表
    public List<Integer> getEnrolledCourseIds(int studentId) {
        List<Integer> courseIds = new ArrayList<>();

        String sql = "SELECT course_id FROM student_courses WHERE student_id = ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                courseIds.add(rs.getInt("course_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courseIds;
    }

    // 获取指定课程的问题列表（通用方法，可用于学生和教师端）
    public List<com.ulp.bean.QuestionWithAnswers> getQuestionsByCourseIdGeneric(int courseId) {
        List<com.ulp.bean.QuestionWithAnswers> questions = new ArrayList<>();

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

                questions.add(new com.ulp.bean.QuestionWithAnswers(question, studentName, answers));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }

}
