package com.ulp.dao;

import com.ulp.bean.AnswerModel;
import com.ulp.bean.CourseWithQuestionCount;
import com.ulp.bean.QuestionModel;
import com.ulp.bean.QuestionWithAnswers;
import com.ulp.servlet.TeacherQuestionServlet;

import java.util.List;

public interface QuestionDao {
    public boolean saveQuestion(QuestionModel question);
    public QuestionModel getQuestionById(int id);
    public List<TeacherQuestionServlet.CourseWithQuestionCount> getCoursesByTeacherId(int teacherId);
    public List<TeacherQuestionServlet.QuestionWithAnswers> getQuestionsByCourseId(int courseId);
    public List<AnswerModel> getAnswersByQuestionId(int questionId);
    public int getUnansweredQuestionCountByCourseId(int courseId);
    public int getQuestionCountByCourseId(int courseId);

    // 学生端相关方法
    public List<CourseWithQuestionCount> getCoursesByStudentId(int studentId);
    public List<CourseWithQuestionCount> getCoursesByTeacherIdGeneric(int teacherId);
    public List<QuestionWithAnswers> getQuestionsByCourseIdGeneric(int courseId);
    public List<Integer> getEnrolledCourseIds(int studentId);

    }
