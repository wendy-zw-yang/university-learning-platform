package com.ulp.bean;

import java.sql.Timestamp;

public class NotificationModel {
    private int id;
    private int userId; // 接收通知的用户ID（学生ID）
    private String type; // 通知类型，如 "answer"
    private String message; // 通知消息内容
    private int relatedId; // 关联ID（如question_id）
    private boolean isRead; // 是否已读
    private Timestamp createdAt; // 创建时间

    public NotificationModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(int relatedId) {
        this.relatedId = relatedId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
