package com.ulp.dao.impl;

import com.ulp.bean.AnswerModel;
import com.ulp.dao.AnswerDao;
import com.ulp.util.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnswerDaoImpl implements AnswerDao {
    
    @Override
    // 保存回答到数据库
    public boolean saveAnswer(AnswerModel answer) {
        String sql = "INSERT INTO answers (content, attachment, question_id, teacher_id, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, answer.getContent());
            pstmt.setString(2, answer.getAttachment());
            pstmt.setInt(3, answer.getQuestionId());
            pstmt.setInt(4, answer.getTeacherId());
            pstmt.setTimestamp(5, answer.getCreatedAt());

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<AnswerModel> getAnswersByQuestionId(int questionId) {
        List<AnswerModel> answerList = new ArrayList<>();
        
        String sql = "SELECT * FROM answers WHERE question_id = ? ORDER BY created_at ASC";
        
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
                answerList.add(answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return answerList;
    }
}
