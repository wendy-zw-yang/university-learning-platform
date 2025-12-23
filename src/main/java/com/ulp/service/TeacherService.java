package com.ulp.service;

import com.ulp.bean.TeacherModel;
import com.ulp.util.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 教师服务类 - 处理教师相关的业务逻辑
 */
public class TeacherService {

    /**
     * 获取所有教师列表
     * @return 教师列表
     */
    public List<TeacherModel> getAllTeachers() {
        List<TeacherModel> teachers = new ArrayList<>();
        String sql = "SELECT id, username, password, role, email, avatar, profile, title, created_at " +
                     "FROM users WHERE role = 'teacher' ORDER BY created_at DESC";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                TeacherModel teacher = new TeacherModel();
                teacher.setId(rs.getInt("id"));
                teacher.setUsername(rs.getString("username"));
                teacher.setPassword(rs.getString("password"));
                teacher.setRole(rs.getString("role"));
                teacher.setEmail(rs.getString("email"));
                teacher.setAvatar(rs.getString("avatar"));
                teacher.setProfile(rs.getString("profile"));
                teacher.setTitle(rs.getString("title"));
                teacher.setCreatedAt(rs.getTimestamp("created_at"));
                
                // 获取教师关联的课程
                teacher.setCourseIds(getCourseIdsByTeacherId(rs.getInt("id")));
                
                teachers.add(teacher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return teachers;
    }

    /**
     * 根据ID获取教师
     * @param id 教师ID
     * @return 教师对象，如果不存在返回null
     */
    public TeacherModel getTeacherById(int id) {
        String sql = "SELECT id, username, password, role, email, avatar, profile, title, created_at " +
                     "FROM users WHERE id = ? AND role = 'teacher'";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                TeacherModel teacher = new TeacherModel();
                teacher.setId(rs.getInt("id"));
                teacher.setUsername(rs.getString("username"));
                teacher.setPassword(rs.getString("password"));
                teacher.setRole(rs.getString("role"));
                teacher.setEmail(rs.getString("email"));
                teacher.setAvatar(rs.getString("avatar"));
                teacher.setProfile(rs.getString("profile"));
                teacher.setTitle(rs.getString("title"));
                teacher.setCreatedAt(rs.getTimestamp("created_at"));
                
                // 获取教师关联的课程
                teacher.setCourseIds(getCourseIdsByTeacherId(id));
                
                return teacher;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * 添加新教师
     * @param teacher 教师对象
     * @return 是否添加成功
     */
    public boolean addTeacher(TeacherModel teacher) {
        String sql = "INSERT INTO users (username, password, role, email, avatar, profile, title) " +
                     "VALUES (?, ?, 'teacher', ?, ?, ?, ?)";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, teacher.getUsername());
            pstmt.setString(2, teacher.getPassword());
            pstmt.setString(3, teacher.getEmail());
            pstmt.setString(4, teacher.getAvatar());
            pstmt.setString(5, teacher.getProfile());
            pstmt.setString(6, teacher.getTitle());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                // 获取生成的教师ID
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int teacherId = rs.getInt(1);
                    teacher.setId(teacherId);
                    
                    // 如果有课程ID，分配课程
                    if (teacher.getCourseIds() != null && !teacher.getCourseIds().isEmpty()) {
                        assignCourses(teacherId, teacher.getCourseIds());
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * 更新教师信息
     * @param teacher 教师对象
     * @return 是否更新成功
     */
    public boolean updateTeacher(TeacherModel teacher) {
        String sql = "UPDATE users SET username = ?, email = ?, avatar = ?, profile = ?, title = ? " +
                     "WHERE id = ? AND role = 'teacher'";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, teacher.getUsername());
            pstmt.setString(2, teacher.getEmail());
            pstmt.setString(3, teacher.getAvatar());
            pstmt.setString(4, teacher.getProfile());
            pstmt.setString(5, teacher.getTitle());
            pstmt.setInt(6, teacher.getId());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                // 更新课程关联
                if (teacher.getCourseIds() != null) {
                    // 先删除旧的关联
                    removeAllCourses(teacher.getId());
                    // 再添加新的关联
                    if (!teacher.getCourseIds().isEmpty()) {
                        assignCourses(teacher.getId(), teacher.getCourseIds());
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * 删除教师
     * @param id 教师ID
     * @return 是否删除成功
     */
    public boolean deleteTeacher(int id) {
        String sql = "DELETE FROM users WHERE id = ? AND role = 'teacher'";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 为教师分配课程
     * @param teacherId 教师ID
     * @param courseIds 课程ID列表
     * @return 是否分配成功
     */
    public boolean assignCourses(int teacherId, List<Integer> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return true;
        }
        
        String sql = "INSERT INTO teacher_courses (teacher_id, course_id) VALUES (?, ?)";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (Integer courseId : courseIds) {
                pstmt.setInt(1, teacherId);
                pstmt.setInt(2, courseId);
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除教师的所有课程关联
     * @param teacherId 教师ID
     * @return 是否移除成功
     */
    public boolean removeAllCourses(int teacherId) {
        String sql = "DELETE FROM teacher_courses WHERE teacher_id = ?";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teacherId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取教师关联的课程ID列表
     * @param teacherId 教师ID
     * @return 课程ID列表
     */
    public List<Integer> getCourseIdsByTeacherId(int teacherId) {
        List<Integer> courseIds = new ArrayList<>();
        String sql = "SELECT course_id FROM teacher_courses WHERE teacher_id = ?";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                courseIds.add(rs.getInt("course_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return courseIds;
    }
}
