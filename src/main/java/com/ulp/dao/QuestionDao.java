package com.ulp.dao;

import com.ulp.bean.AnswerModel;
import com.ulp.bean.CourseWithQuestionCount;
import com.ulp.bean.QuestionModel;
import com.ulp.bean.QuestionWithAnswers;

import java.util.List;

public interface QuestionDao {
    public boolean saveQuestion(QuestionModel question);
    public QuestionModel getQuestionById(int id);
    public List<com.ulp.bean.CourseWithQuestionCount> getCoursesByTeacherId(int teacherId);
    public List<com.ulp.bean.QuestionWithAnswers> getQuestionsByCourseId(int courseId);
    public List<AnswerModel> getAnswersByQuestionId(int questionId);
    public int getUnansweredQuestionCountByCourseId(int courseId);
    public int getQuestionCountByCourseId(int courseId);

    // 学生端相关方法
    public List<CourseWithQuestionCount> getCoursesByStudentId(int studentId);
    public List<CourseWithQuestionCount> getCoursesByTeacherIdGeneric(int teacherId);
    public List<QuestionWithAnswers> getQuestionsByCourseIdGeneric(int courseId);
    public List<Integer> getEnrolledCourseIds(int studentId);
    public List<com.ulp.bean.QuestionWithAnswers> getQuestionsByStudentId(int studentId);
    public boolean updateQuestionAttachment(int questionId, String newAttachment);


    // 管理员功能相关方法
    public List<CourseWithQuestionCount> getAllCoursesWithQuestionCount();
    public boolean deleteQuestionById(int questionId);
    public boolean deleteAnswerById(int answerId);
    public boolean updateQuestionContent(int questionId, String newContent);
    public boolean updateAnswerContent(int answerId, String newContent);

    // 教师端相关方法
    public AnswerModel getAnswerById(int answerId);
}