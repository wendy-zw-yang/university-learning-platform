package com.ulp.service;

import com.ulp.bean.AnswerModel;
import com.ulp.dao.impl.AnswerDaoImpl;

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
}