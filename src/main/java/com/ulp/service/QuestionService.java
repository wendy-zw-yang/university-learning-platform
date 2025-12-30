package com.ulp.service;

import com.ulp.bean.AnswerModel;
import com.ulp.bean.CourseWithQuestionCount;
import com.ulp.bean.QuestionModel;
import com.ulp.bean.QuestionWithAnswers;
import com.ulp.dao.QuestionDao;
import com.ulp.dao.impl.QuestionDaoImpl;
import com.ulp.servlet.TeacherQuestionServlet;

import java.util.List;

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

    // 根据教师ID获取课程列表
    public List<com.ulp.bean.CourseWithQuestionCount> getCoursesByTeacherId(int teacherId) {
        QuestionDao questionDao = new QuestionDaoImpl();
        return questionDao.getCoursesByTeacherId(teacherId);
    }

    public List<com.ulp.bean.QuestionWithAnswers> getQuestionsByCourseId(int courseId) {
        QuestionDao questionDao = new QuestionDaoImpl();
        return questionDao.getQuestionsByCourseId(courseId);
    }

    public List<AnswerModel> getAnswersByQuestionId(int questionId) {
        QuestionDao questionDao = new QuestionDaoImpl();
        return questionDao.getAnswersByQuestionId(questionId);
    }

    public int getUnansweredQuestionCountByCourseId(int courseId) {
        QuestionDao questionDao = new QuestionDaoImpl();
        return questionDao.getUnansweredQuestionCountByCourseId(courseId);
    }

    public int getQuestionCountByCourseId(int courseId) {
        QuestionDao questionDao = new QuestionDaoImpl();
        return questionDao.getQuestionCountByCourseId(courseId);
    }
    
    public List<CourseWithQuestionCount> getCoursesByStudentId(int studentId) {
        QuestionDao questionDao = new QuestionDaoImpl();
        return questionDao.getCoursesByStudentId(studentId);
    }
    
    public List<QuestionWithAnswers> getQuestionsByCourseIdForStudent(int courseId) {
        QuestionDaoImpl questionDao = new QuestionDaoImpl();
        return questionDao.getQuestionsByCourseIdGeneric(courseId);
    }
    
    public List<Integer> getEnrolledCourseIds(int studentId) {
        QuestionDaoImpl questionDao = new QuestionDaoImpl();
        return questionDao.getEnrolledCourseIds(studentId);
    }
    
    // 管理员功能：获取所有课程及问题数量
    public List<CourseWithQuestionCount> getAllCoursesWithQuestionCount() {
        QuestionDaoImpl questionDao = new QuestionDaoImpl();
        return questionDao.getAllCoursesWithQuestionCount();
    }
    
    // 管理员功能：根据课程ID获取问题列表
    public List<QuestionWithAnswers> getQuestionsByCourseIdForAdmin(int courseId) {
        QuestionDaoImpl questionDao = new QuestionDaoImpl();
        return questionDao.getQuestionsByCourseIdGeneric(courseId);
    }
    
    // 管理员功能：删除问题（包括相关回答）
    public boolean deleteQuestionById(int questionId) {
        QuestionDaoImpl questionDao = new QuestionDaoImpl();
        return questionDao.deleteQuestionById(questionId);
    }
    
    // 管理员功能：删除回答
    public boolean deleteAnswerById(int answerId) {
        QuestionDaoImpl questionDao = new QuestionDaoImpl();
        return questionDao.deleteAnswerById(answerId);
    }

    public boolean updateQuestionContent(int questionId, String newContent) {
        QuestionDaoImpl questionDao = new QuestionDaoImpl();
        return questionDao.updateQuestionContent(questionId, newContent);
    }

    public boolean updateAnswerContent(int answerId, String newContent) {
        QuestionDaoImpl questionDao = new QuestionDaoImpl();
        return questionDao.updateAnswerContent(answerId, newContent);
    }
}