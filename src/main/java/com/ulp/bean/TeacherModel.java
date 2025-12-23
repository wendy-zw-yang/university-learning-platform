package com.ulp.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 教师模型类 - 继承自UserModel，包含教师特定数据
 */
public class TeacherModel extends UserModel {
    private String profile;  // 教师简介
    private String title;    // 教师职称
    private List<Integer> courseIds;  // 教师关联的课程ID列表

    // 构造函数
    public TeacherModel() {
        super();
        this.courseIds = new ArrayList<>();
    }

    public TeacherModel(int id, String username, String password, String role, 
                       String email, String avatar, String profile, String title) {
        super(id, username, password, role, email, avatar);
        this.profile = profile;
        this.title = title;
        this.courseIds = new ArrayList<>();
    }

    // Getters and Setters
    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Integer> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<Integer> courseIds) {
        this.courseIds = courseIds;
    }

    /**
     * 添加课程ID到教师的课程列表
     * @param courseId 课程ID
     */
    public void addCourse(int courseId) {
        if (this.courseIds == null) {
            this.courseIds = new ArrayList<>();
        }
        if (!this.courseIds.contains(courseId)) {
            this.courseIds.add(courseId);
        }
    }

    /**
     * 移除课程ID
     * @param courseId 课程ID
     */
    public void removeCourse(int courseId) {
        if (this.courseIds != null) {
            this.courseIds.remove(Integer.valueOf(courseId));
        }
    }

    @Override
    public String toString() {
        return "TeacherModel{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", role='" + getRole() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", profile='" + profile + '\'' +
                ", title='" + title + '\'' +
                ", courseIds=" + courseIds +
                '}';
    }
}
