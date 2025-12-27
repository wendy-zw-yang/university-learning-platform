package com.ulp.bean;


import java.sql.Timestamp;

public class AnswerModel {

    /**
     -- ulpdb.answers definition

     CREATE TABLE `answers` (
     `id` int NOT NULL AUTO_INCREMENT COMMENT '回答ID，主键，自增',
     `content` text NOT NULL COMMENT '回答内容',
     `attachment` varchar(255) DEFAULT NULL COMMENT '附件路径（e.g., 图片）',
     `question_id` int NOT NULL COMMENT '所属问题ID',
     `teacher_id` int NOT NULL COMMENT '回答教师ID',
     `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     PRIMARY KEY (`id`),
     KEY `question_id` (`question_id`),
     KEY `teacher_id` (`teacher_id`),
     CONSTRAINT `answers_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
     CONSTRAINT `answers_ibfk_2` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='回答表';
     */
    private int id;
    private String content;
    private String attachment;
    private int questionId;
    private int teacherId;
    private Timestamp createdAt;
    private String teacherName;

    public AnswerModel()
    {
        super();
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

}
