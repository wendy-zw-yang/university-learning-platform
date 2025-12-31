package com.ulp.dao;

import com.ulp.bean.NotificationModel;
import java.util.List;

public interface NotificationDao {
    boolean saveNotification(NotificationModel notification);
    List<NotificationModel> getNotificationsByUserId(int userId);
    List<NotificationModel> getUnreadNotificationsByUserId(int userId);
    boolean markAsRead(int notificationId);
    boolean markAllAsRead(int userId);
    boolean deleteNotification(int notificationId);
    int getUnreadCountByUserId(int userId);
    List<NotificationModel> getNotificationsByUserIdAndType(int userId, String type);
    int getUnreadCountByUserIdAndType(int userId, String type);
    List<NotificationModel> getNotificationsByUserIdAndTypeAndRelatedId(int userId, String type, int relatedId);
    boolean markAsReadByUserIdAndType(int userId, String type);
}
