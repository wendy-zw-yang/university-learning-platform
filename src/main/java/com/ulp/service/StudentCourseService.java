package com.ulp.service;

import com.ulp.bean.CourseModel;
import com.ulp.dao.StudentCourseDao;
import com.ulp.dao.impl.StudentCourseDaoImpl;

import java.util.List;

public class StudentCourseService {

    private StudentCourseDao studentCourseDao;

    public StudentCourseService() {
        this.studentCourseDao = new StudentCourseDaoImpl();
    }

    /**
     * 获取所有课程
     */
    public List<CourseModel> getAllCourses() {
        CourseService courseService = new CourseService();
        return courseService.getAllCourses();
    }

    /**
     * 获取学生已选课程ID列表
     */
    public List<Integer> getEnrolledCourseIds(int studentId) {
        return studentCourseDao.getEnrolledCourseIds(studentId);
    }

    /**
     * 学生选课
     */
    public boolean enrollCourse(int studentId, int courseId) {
        return studentCourseDao.enrollCourse(studentId, courseId);
    }

    /**
     * 学生退课
     */
    public boolean unenrollCourse(int studentId, int courseId) {
        return studentCourseDao.unenrollCourse(studentId, courseId);
    }

    /**
     * 检查学生是否已选某课程
     */
    public boolean isStudentEnrolled(int studentId, int courseId) {
        return studentCourseDao.isStudentEnrolled(studentId, courseId);
    }
}
