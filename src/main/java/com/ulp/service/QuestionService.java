package com.ulp.service;

import com.ulp.bean.QuestionModel;
import com.ulp.dao.QuestionDao;
import com.ulp.dao.impl.QuestionDaoImpl;

public class QuestionService {
    // 添加问题到数据库
    public boolean addQuestion(QuestionModel question) {
        QuestionDaoImpl questionDao = new QuestionDaoImpl();
        return questionDao.saveQuestion(question);
    }
    
    // 根据ID获取问题
    public QuestionModel getQuestionById(int id) {
        // 这里需要添加一个根据ID获取问题的方法到DAO中
        QuestionDaoImpl questionDao = new QuestionDaoImpl();
        return questionDao.getQuestionById(id);
    }
}
