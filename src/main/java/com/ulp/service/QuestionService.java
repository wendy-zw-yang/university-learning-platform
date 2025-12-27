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
}
