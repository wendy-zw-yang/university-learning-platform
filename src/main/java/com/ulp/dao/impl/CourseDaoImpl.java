package com.ulp.dao.impl;

import com.ulp.bean.CourseModel;
import com.ulp.dao.CourseDao;
import com.ulp.util.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private CourseModel extractCourseFromResultSet(ResultSet rs) throws SQLException{
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

    // 根据ID获取课程信息
    public CourseModel getCourseById(int courseId) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        try (Connection conn = DBHelper.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
