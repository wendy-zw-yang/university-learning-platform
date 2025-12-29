package com.ulp.bean;

import java.util.List;

// 用于封装问题和回答的通用类
public class QuestionWithAnswers {
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