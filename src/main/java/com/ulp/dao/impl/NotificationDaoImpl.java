package com.ulp.dao.impl;

import com.ulp.bean.NotificationModel;
import com.ulp.dao.NotificationDao;
import com.ulp.util.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDaoImpl implements NotificationDao {

    @Override
    public boolean saveNotification(NotificationModel notification) {
        String sql = "INSERT INTO notifications (user_id, type, message, related_id, is_read, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, notification.getUserId());
            pstmt.setString(2, notification.getType());
            pstmt.setString(3, notification.getMessage());
            pstmt.setInt(4, notification.getRelatedId());
            pstmt.setBoolean(5, notification.isRead());
            pstmt.setTimestamp(6, notification.getCreatedAt());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    notification.setId(rs.getInt(1));
                }
            }
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<NotificationModel> getNotificationsByUserId(int userId) {
        List<NotificationModel> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                NotificationModel notification = new NotificationModel();
                notification.setId(rs.getInt("id"));
                notification.setUserId(rs.getInt("user_id"));
                notification.setType(rs.getString("type"));
                notification.setMessage(rs.getString("message"));
                notification.setRelatedId(rs.getInt("related_id"));
                notification.setRead(rs.getBoolean("is_read"));
                notification.setCreatedAt(rs.getTimestamp("created_at"));
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    @Override
    public List<NotificationModel> getUnreadNotificationsByUserId(int userId) {
        List<NotificationModel> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = false ORDER BY created_at DESC";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                NotificationModel notification = new NotificationModel();
                notification.setId(rs.getInt("id"));
                notification.setUserId(rs.getInt("user_id"));
                notification.setType(rs.getString("type"));
                notification.setMessage(rs.getString("message"));
                notification.setRelatedId(rs.getInt("related_id"));
                notification.setRead(rs.getBoolean("is_read"));
                notification.setCreatedAt(rs.getTimestamp("created_at"));
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    @Override
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = true WHERE id = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = true WHERE user_id = ? AND is_read = false";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteNotification(int notificationId) {
        String sql = "DELETE FROM notifications WHERE id = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getUnreadCountByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = false";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<NotificationModel> getNotificationsByUserIdAndType(int userId, String type) {
        List<NotificationModel> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND type = ? ORDER BY created_at DESC";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, type);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                NotificationModel notification = new NotificationModel();
                notification.setId(rs.getInt("id"));
                notification.setUserId(rs.getInt("user_id"));
                notification.setType(rs.getString("type"));
                notification.setMessage(rs.getString("message"));
                notification.setRelatedId(rs.getInt("related_id"));
                notification.setRead(rs.getBoolean("is_read"));
                notification.setCreatedAt(rs.getTimestamp("created_at"));
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    @Override
    public int getUnreadCountByUserIdAndType(int userId, String type) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND type = ? AND is_read = false";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, type);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<NotificationModel> getNotificationsByUserIdAndTypeAndRelatedId(int userId, String type, int relatedId) {
        List<NotificationModel> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND type = ? AND related_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, type);
            pstmt.setInt(3, relatedId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                NotificationModel notification = new NotificationModel();
                notification.setId(rs.getInt("id"));
                notification.setUserId(rs.getInt("user_id"));
                notification.setType(rs.getString("type"));
                notification.setMessage(rs.getString("message"));
                notification.setRelatedId(rs.getInt("related_id"));
                notification.setRead(rs.getBoolean("is_read"));
                notification.setCreatedAt(rs.getTimestamp("created_at"));
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    @Override
    public boolean markAsReadByUserIdAndType(int userId, String type) {
        String sql = "UPDATE notifications SET is_read = true WHERE user_id = ? AND type = ? AND is_read = false";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, type);
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
