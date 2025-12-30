package com.ulp.dao;

import com.ulp.bean.TeacherModel;

import java.util.List;

public interface TeacherDao {

    public boolean removeAllCourses(int teacherId);
    public boolean assignCourses(int teacherId, List<Integer> courseIds);
    public boolean assignCoursesToTeacher(int teacherId, List<Integer> courseIds);
    public List<TeacherModel> getAllTeachers();
}
