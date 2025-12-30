package com.ulp.bean;

import java.sql.Timestamp;

/**
 * 课程模型类 - 表示课程数据
 */
public class CourseModel {
    private int id;
    private String name;
    private Integer teacherId;
    private String description; // 课程描述
    private String college;
    private String visibility; // "class_only" or "all"
    private Timestamp createdAt;

    // 构造函数
    public CourseModel() {
    }

    public CourseModel(int id, String name, Integer teacherId, String description, 
                      String college, String visibility) {
        this.id = id;
        this.name = name;
        this.teacherId = teacherId;
        this.description = description;
        this.college = college;
        this.visibility = visibility;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CourseModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", teacherId=" + teacherId +
                ", description='" + description + '\'' +
                ", college='" + college + '\'' +
                ", visibility='" + visibility + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
