package com.ulp.dao.impl;

import com.ulp.bean.CourseModel;
import com.ulp.dao.CourseDao;
import com.ulp.util.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDaoImpl implements CourseDao {
    public CourseDaoImpl() {
    }

    @Override
    public List<CourseModel> getCoursesByTeacherId(int teacherId) {
        List<CourseModel> courseList = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE teacher_id = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                courseList.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseList;
    }

    @Override
    // 根据ID获取课程信息
    public CourseModel getCourseById(int courseId) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        try (Connection conn = DBHelper.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractCourseFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取所有课程列表
     * @return 课程列表
     */
    @Override
    public List<CourseModel> getAllCourses() {
        List<CourseModel> courses = new ArrayList<>();
        String sql = "SELECT id, name, teacher_id, description, college, visibility, created_at FROM courses ORDER BY created_at DESC";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }

    @Override
    public List<CourseModel> getUnassignedCourses() {
        List<CourseModel> courseList = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE teacher_id IS NULL OR teacher_id = 0";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                courseList.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseList;
    }

    /**
     * 添加新课程
     * @param course 课程对象
     * @return 是否添加成功
     */
    @Override
    public boolean addCourse(CourseModel course) {
        String sql = "INSERT INTO courses (name, teacher_id, description, college, visibility) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course.getName());
            if (course.getTeacherId() != null) {
                pstmt.setInt(2, course.getTeacherId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, course.getDescription());
            pstmt.setString(4, course.getCollege());
            pstmt.setString(5, course.getVisibility());

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新课程信息
     * @param course 课程对象
     * @return 是否更新成功
     */
    @Override
    public boolean updateCourse(CourseModel course) {
        String sql = "UPDATE courses SET name = ?, teacher_id = ?, description = ?, college = ?, visibility = ? WHERE id = ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course.getName());
            if (course.getTeacherId() != null) {
                pstmt.setInt(2, course.getTeacherId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, course.getDescription());
            pstmt.setString(4, course.getCollege());
            pstmt.setString(5, course.getVisibility());
            pstmt.setInt(6, course.getId());

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除课程
     * @param id 课程ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteCourse(int id) {
        String sql = "DELETE FROM courses WHERE id = ?";

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

    private CourseModel extractCourseFromResultSet(ResultSet rs) throws SQLException {
        CourseModel course = new CourseModel();
        course.setId(rs.getInt("id"));
        course.setName(rs.getString("name"));
        course.setTeacherId(rs.getInt("teacher_id"));
        course.setDescription(rs.getString("description"));
        course.setCollege(rs.getString("college"));
        course.setVisibility(rs.getString("visibility"));
        course.setCreatedAt(rs.getTimestamp("created_at"));
        return course;
    }

}
