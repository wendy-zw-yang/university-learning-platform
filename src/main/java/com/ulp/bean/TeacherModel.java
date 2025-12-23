package com.ulp.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 教师模型类 - 继承自UserModel，包含教师特定数据
 */
public class TeacherModel {

    UserModel userModel;
    private List<CourseModel> courseList;  // 教师关联的课程列表

    private List<Integer> courseIdList;  // 教师关联的课程ID列表

    // 构造函数
    public TeacherModel() {
        super();
        this.courseList = new ArrayList<>();
        this.courseIdList=new ArrayList<>();
    }

    public TeacherModel(UserModel userModel){

        this.userModel = userModel;
        this.courseList = new ArrayList<>();
        this.courseIdList=new ArrayList<>();
    }

    public List<CourseModel> getCourseList() {
        return courseList;
    }


    public void setCourseList(List<CourseModel> courseList) {
        this.courseList = courseList;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
    public List<Integer> getCourseIdList() {
        return courseIdList;
    }

    public void setCourseIdList(List<Integer> courseIdList) {
        this.courseIdList = courseIdList;
    }

    /**
     * 添加课程到教师的课程列表
     * @param course 课程对象
     */
    public void addCourse(CourseModel course) {
        if (this.courseList == null) {
            this.courseList = new ArrayList<>();
        }
        if (!this.courseList.contains(course)) {
            this.courseList.add(course);
        }
    }

    /**
     * 移除课程
     * @param courseId 课程ID
     */
    public void removeCourse(int courseId) {
        if (this.courseList != null) {
            for(CourseModel each : this.courseList)
                if( each.getId() == courseId)
                    this.courseList.remove(each);
        }
    }

    @Override
    public String toString() {
        return "TeacherModel{" +
                "userModel=" + userModel +
                ", courseList=" + courseList +
                '}';
    }
}
