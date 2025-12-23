package com.ulp.dao.impl;

import com.ulp.bean.CourseModel;
import com.ulp.bean.TeacherModel;
import com.ulp.bean.UserModel;
import com.ulp.dao.CourseDao;
import com.ulp.dao.TeacherDao;
import com.ulp.dao.UserDao;
import com.ulp.util.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeacherDaoImpl implements TeacherDao {
    public TeacherDaoImpl() {
    }
    /**
     * 移除教师的所有课程关联
     * @param teacherId 教师ID
     * @return 是否移除成功
     */
    @Override
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
     * 为教师分配课程
     * @param teacherId 教师ID
     * @param courseIds 课程ID列表
     * @return 是否分配成功
     */
    @Override
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
     * 获取所有教师列表
     * @return 教师列表
     */
    @Override
    public List<TeacherModel> getAllTeachers() {
        List<TeacherModel> teachers = new ArrayList<>();
        String sql = "SELECT id, username, password, role, email, avatar, profile, title, created_at " +
                "FROM users WHERE role = 'teacher' ORDER BY created_at DESC";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                TeacherModel teacher = new TeacherModel();
                UserModel user = new UserModel();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setEmail(rs.getString("email"));
                user.setAvatar(rs.getString("avatar"));
                user.setProfile(rs.getString("profile"));
                user.setTitle(rs.getString("title"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                teacher.setUserModel( user);
                // 获取教师关联的课程
                teacher.setCourseList(new CourseDaoImpl().getCoursesByTeacherId(user.getId()));

                teachers.add(teacher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return teachers;
    }

}
