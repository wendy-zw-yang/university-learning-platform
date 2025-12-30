package com.ulp.service;

import com.ulp.bean.CourseModel;
import com.ulp.bean.TeacherModel;
import com.ulp.bean.UserModel;
import com.ulp.dao.CourseDao;
import com.ulp.dao.TeacherDao;
import com.ulp.dao.UserDao;
import com.ulp.dao.impl.CourseDaoImpl;
import com.ulp.dao.impl.TeacherDaoImpl;
import com.ulp.dao.impl.UserDaoImpl;
import com.ulp.util.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 教师服务类 - 处理教师相关的业务逻辑
 */
public class TeacherService {

    /**
     * 根据ID获取教师
     * @param id 教师ID
     * @return 教师对象，如果不存在返回null
     */
    public TeacherModel getTeacherById(int id) {
        TeacherModel teacher = new TeacherModel();
        UserDao userDao = new UserDaoImpl();
        UserModel user = userDao.findUserById(id);
        teacher.setUserModel(user);

        // 设置课程列表
        List<CourseModel> courseList = getCoursesByTeacherId(id);
        teacher.setCourseList(courseList);

        // 设置课程ID列表
        List<Integer> courseIdList = new ArrayList<>();
        for (CourseModel course : courseList) {
            courseIdList.add(course.getId());
        }
        teacher.setCourseIdList(courseIdList);

        return teacher;
    }

    /**
     * 获取教师关联的课程ID列表
     * @param teacherId 教师ID
     * @return 课程ID列表
     */
    public List<CourseModel> getCoursesByTeacherId(int teacherId) {
        CourseDao courseDao = new CourseDaoImpl();
        return courseDao.getCoursesByTeacherId(teacherId);
    }

    /**
     * 获取所有教师列表
     * @return 教师列表
     */
    public List<TeacherModel> getAllTeachers() {
        List<TeacherModel> teachers = new ArrayList<>();
        teachers=new TeacherDaoImpl().getAllTeachers();
        return teachers;
    }

    /**
     * 添加新教师
     * @param teacher 教师对象
     * @return 是否添加成功
     */
    public boolean addTeacher(TeacherModel teacher) {
        UserModel user = teacher.getUserModel();
        user.setRole("teacher");
        UserDao userDao = new UserDaoImpl();
        return userDao.insertUser(user);
    }

    /**
     * 更新教师信息
     * @param teacher 教师对象
     * @return 是否更新成功
     */
    public boolean updateTeacher(TeacherModel teacher) {
        UserModel user = teacher.getUserModel();
        user.setRole("teacher");
        UserDao userDao = new UserDaoImpl();
        return userDao.updateProfileInDatabase(user);
    }

    /**
     * 删除教师
     * @param id 教师ID
     * @return 是否删除成功
     */
    public boolean deleteTeacher(int id) {
        UserDao userDao = new UserDaoImpl();

        //todo: 删除教师关联的课程
        TeacherDao teacherDao = new TeacherDaoImpl();
        teacherDao.removeAllCourses(id); // 移除教师的所有课程关联
        return userDao.deleteUserById(id);
    }

    /**
     * 为教师分配课程
     * @param teacherId 教师ID
     * @param courseIds 课程ID列表
     * @return 是否分配成功
     */
    public boolean assignCourses(int teacherId, List<Integer> courseIds) {
        TeacherDao teacherDao = new TeacherDaoImpl();
        return teacherDao.assignCoursesToTeacher(teacherId, courseIds);
    }

    /**
     * 移除教师的所有课程关联
     * @param teacherId 教师ID
     * @return 是否移除成功
     */
    public boolean removeAllCourses(int teacherId) {
        TeacherDao teacherDao = new TeacherDaoImpl();
        return teacherDao.removeAllCourses(teacherId);
    }

}
