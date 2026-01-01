package com.ulp.service;

import com.ulp.bean.AnswerModel;
import com.ulp.dao.impl.AnswerDaoImpl;
import com.ulp.util.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

public class AnswerService {
    
    // 添加回答到数据库
    public boolean addAnswer(AnswerModel answer) {
        // 设置创建时间
        answer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        
        AnswerDaoImpl answerDao = new AnswerDaoImpl();
        return answerDao.saveAnswer(answer);
    }
    
    // 根据问题ID获取回答列表
    public List<AnswerModel> getAnswersByQuestionId(int questionId) {
        AnswerDaoImpl answerDao = new AnswerDaoImpl();
        return answerDao.getAnswersByQuestionId(questionId);
    }

    // 更新回答内容和附件
    public boolean updateAnswer(int answerId, String newContent, String newAttachment) {
        String sql = "UPDATE answers SET content = ?, attachment = ? WHERE id = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newContent);
            pstmt.setString(2, newAttachment);
            pstmt.setInt(3, answerId);

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}