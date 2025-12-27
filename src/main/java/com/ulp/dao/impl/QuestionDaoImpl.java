package com.ulp.dao.impl;

import com.ulp.bean.QuestionModel;
import com.ulp.dao.QuestionDao;
import com.ulp.util.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
