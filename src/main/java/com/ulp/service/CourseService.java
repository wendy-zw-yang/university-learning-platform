package com.ulp.service;

import com.ulp.bean.CourseModel;
import com.ulp.dao.CourseDao;
import com.ulp.dao.impl.CourseDaoImpl;
import com.ulp.util.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程服务类 - 处理课程相关的业务逻辑
 */
public class CourseService {
    /**
     * 获取所有课程列表
     * @return 课程列表
     */
    public List<CourseModel> getAllCourses() {
        return new CourseDaoImpl().getAllCourses();
    }

    /**
     * 根据ID获取课程
     * @param id 课程ID
     * @return 课程对象，如果不存在返回null
     */
    public CourseModel getCourseById(int id) {
        CourseDao courseDao = new CourseDaoImpl();
        return courseDao.getCourseById(id);
    }

    /**
     * 添加新课程
     * @param course 课程对象
     * @return 是否添加成功
     */
    public boolean addCourse(CourseModel course) {
        CourseDao courseDao = new CourseDaoImpl();
        return courseDao.addCourse(course);
    }

    /**
     * 更新课程信息
     * @param course 课程对象
     * @return 是否更新成功
     */
    public boolean updateCourse(CourseModel course) {
        CourseDao courseDao = new CourseDaoImpl();
        return courseDao.updateCourse(course);
    }

    /**
     * 删除课程
     * @param id 课程ID
     * @return 是否删除成功
     */
    public boolean deleteCourse(int id) {
        CourseDao courseDao = new CourseDaoImpl();
        return courseDao.deleteCourse(id);
    }

    /**
     * 根据教师ID获取课程列表
     * @param teacherId 教师ID
     * @return 课程列表
     */
    public List<CourseModel> getCoursesByTeacherId(int teacherId) {
        CourseDao courseDao = new CourseDaoImpl();
        return courseDao.getCoursesByTeacherId(teacherId);
    }
}
