package com.ulp.dao;

import com.ulp.bean.AnswerModel;

public interface AnswerDao {
    boolean saveAnswer(AnswerModel answer);
    java.util.List<AnswerModel> getAnswersByQuestionId(int questionId);
    public AnswerModel getAnswerById(int answerId);
}
