package com.ulp.dao;

import com.ulp.bean.CourseModel;

import java.util.List;

public interface CourseDao {

    public List<CourseModel> getCoursesByTeacherId(int teacherId);

}
