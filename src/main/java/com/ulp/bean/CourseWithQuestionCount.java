package com.ulp.bean;

// 用于封装课程和问题数量的通用类
public class CourseWithQuestionCount {
    private CourseModel course;
    private String teacherName;
    private int questionCount;
    
    public CourseWithQuestionCount(CourseModel course, String teacherName, int questionCount) {
        this.course = course;
        this.teacherName = teacherName;
        this.questionCount = questionCount;
    }
    
    public CourseModel getCourse() { return course; }
    public void setCourse(CourseModel course) { this.course = course; }
    
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    
    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
}