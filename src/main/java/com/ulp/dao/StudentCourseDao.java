package com.ulp.dao;

import java.util.List;

public interface StudentCourseDao {

    /**
     * 获取学生已选课程ID列表
     */
    List<Integer> getEnrolledCourseIds(int studentId);

    /**
     * 学生选课
     */
    boolean enrollCourse(int studentId, int courseId);

    /**
     * 学生退课
     */
    boolean unenrollCourse(int studentId, int courseId);

    /**
     * 检查学生是否已选某课程
     */
    boolean isStudentEnrolled(int studentId, int courseId);
}
