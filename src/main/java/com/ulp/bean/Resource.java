package com.ulp.bean;

import java.sql.Timestamp;

public class Resource {
    private Integer id;              // 资源ID
    private String title;            // 资源标题
    private String description;      // 资源简介
    private String filePath;         // 文件路径
    private Integer courseId;        // 所属课程ID
    private Integer uploaderId;      // 上传者ID
    private Integer downloadCount;   // 下载次数
    private Timestamp createdAt;     // 创建时间

    public Resource() {}

    public Resource(String title, String description, String filePath,
                   Integer courseId, Integer uploaderId, Integer downloadCount, Timestamp createdAt) {
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.courseId = courseId;
        this.uploaderId = uploaderId;
        this.downloadCount = downloadCount;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(Integer uploaderId) {
        this.uploaderId = uploaderId;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", filePath='" + filePath + '\'' +
                ", courseId=" + courseId +
                ", uploaderId=" + uploaderId +
                ", downloadCount=" + downloadCount +
                ", createdAt=" + createdAt +
                '}';
    }
}
