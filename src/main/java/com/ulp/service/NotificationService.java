package com.ulp.service;

import com.ulp.bean.NotificationModel;
import com.ulp.dao.impl.NotificationDaoImpl;

import java.sql.Timestamp;
import java.util.List;

public class NotificationService {
    private NotificationDaoImpl notificationDao;

    public NotificationService() {
        this.notificationDao = new NotificationDaoImpl();
    }

    // 创建通知
    public boolean createNotification(int userId, String type, String message, int relatedId) {
        NotificationModel notification = new NotificationModel();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setRelatedId(relatedId);
        notification.setRead(false);
        notification.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return notificationDao.saveNotification(notification);
    }

    // 获取用户的所有通知
    public List<NotificationModel> getNotificationsByUserId(int userId) {
        return notificationDao.getNotificationsByUserId(userId);
    }

    // 获取用户未读通知
    public List<NotificationModel> getUnreadNotificationsByUserId(int userId) {
        return notificationDao.getUnreadNotificationsByUserId(userId);
    }

    // 标记通知为已读
    public boolean markAsRead(int notificationId) {
        return notificationDao.markAsRead(notificationId);
    }

    // 标记用户所有通知为已读
    public boolean markAllAsRead(int userId) {
        return notificationDao.markAllAsRead(userId);
    }

    // 删除通知
    public boolean deleteNotification(int notificationId) {
        return notificationDao.deleteNotification(notificationId);
    }

    // 获取用户未读通知数量
    public int getUnreadCountByUserId(int userId) {
        return notificationDao.getUnreadCountByUserId(userId);
    }

    // 获取用户特定类型的通知
    public List<NotificationModel> getNotificationsByUserIdAndType(int userId, String type) {
        return notificationDao.getNotificationsByUserIdAndType(userId, type);
    }

    // 获取用户特定类型的未读通知数量
    public int getUnreadCountByUserIdAndType(int userId, String type) {
        return notificationDao.getUnreadCountByUserIdAndType(userId, type);
    }

    // 获取用户特定类型和关联ID的通知
    public List<NotificationModel> getNotificationsByUserIdAndTypeAndRelatedId(int userId, String type, int relatedId) {
        return notificationDao.getNotificationsByUserIdAndTypeAndRelatedId(userId, type, relatedId);
    }

    // 标记用户特定类型的通知为已读
    public boolean markAsReadByUserIdAndType(int userId, String type) {
        return notificationDao.markAsReadByUserIdAndType(userId, type);
    }
}
