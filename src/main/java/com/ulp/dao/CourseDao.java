package com.ulp.dao;

import com.ulp.bean.CourseModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface CourseDao {

    public List<CourseModel> getCoursesByTeacherId(int teacherId);
    public CourseModel getCourseById(int courseId);
    public List<CourseModel> getAllCourses();
    public boolean addCourse(CourseModel course);
    public boolean updateCourse(CourseModel course);
    public boolean deleteCourse(int id);

    }
