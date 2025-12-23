package com.ulp.Dao.impl;

import com.ulp.Dao.ResourceDao;
import com.ulp.bean.Resource;
import com.ulp.util.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceDaoImpl implements ResourceDao {

    @Override
    public boolean addResource(Resource resource) {
        String sql = "INSERT INTO resources (title, description, file_path, course_id, uploader_id, download_count) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, resource.getTitle());
            pstmt.setString(2, resource.getDescription());
            pstmt.setString(3, resource.getFilePath());
            pstmt.setInt(4, resource.getCourseId());
            pstmt.setInt(5, resource.getUploaderId());
            pstmt.setInt(6, resource.getDownloadCount() != null ? resource.getDownloadCount() : 0);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteResourceById(Integer id) {
        String sql = "DELETE FROM resources WHERE id = ?";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateResource(Resource resource) {
        String sql = "UPDATE resources SET title = ?, description = ?, file_path = ?, course_id = ?, uploader_id = ?, download_count = ? WHERE id = ?";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, resource.getTitle());
            pstmt.setString(2, resource.getDescription());
            pstmt.setString(3, resource.getFilePath());
            pstmt.setInt(4, resource.getCourseId());
            pstmt.setInt(5, resource.getUploaderId());
            pstmt.setInt(6, resource.getDownloadCount() != null ? resource.getDownloadCount() : 0);
            pstmt.setInt(7, resource.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Resource getResourceById(Integer id) {
        String sql = "SELECT * FROM resources WHERE id = ?";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractResourceFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    public List<Resource> getAllResources() {
        String sql = "SELECT * FROM resources ORDER BY created_at DESC";
        List<Resource> resources = new ArrayList<>();
        
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                resources.add(extractResourceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resources;
    }

    @Override
    public List<Resource> getResourcesByCourseId(Integer courseId) {
        String sql = "SELECT * FROM resources WHERE course_id = ? ORDER BY created_at DESC";
        List<Resource> resources = new ArrayList<>();
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                resources.add(extractResourceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resources;
    }

    @Override
    public List<Resource> getResourcesByUploaderId(Integer uploaderId) {
        String sql = "SELECT * FROM resources WHERE uploader_id = ? ORDER BY created_at DESC";
        List<Resource> resources = new ArrayList<>();
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, uploaderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                resources.add(extractResourceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resources;
    }

    @Override
    public boolean incrementDownloadCount(Integer id) {
        String sql = "UPDATE resources SET download_count = download_count + 1 WHERE id = ?";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Resource> searchResourcesByTitle(String title) {
        String sql = "SELECT * FROM resources WHERE title LIKE ? ORDER BY created_at DESC";
        List<Resource> resources = new ArrayList<>();
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                resources.add(extractResourceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resources;
    }

    /**
     * 从ResultSet中提取Resource对象
     * @param rs ResultSet对象
     * @return Resource对象
     * @throws SQLException SQL异常
     */
    private Resource extractResourceFromResultSet(ResultSet rs) throws SQLException {
        Resource resource = new Resource();
        resource.setId(rs.getInt("id"));
        resource.setTitle(rs.getString("title"));
        resource.setDescription(rs.getString("description"));
        resource.setFilePath(rs.getString("file_path"));
        resource.setCourseId(rs.getInt("course_id"));
        resource.setUploaderId(rs.getInt("uploader_id"));
        resource.setDownloadCount(rs.getInt("download_count"));
        resource.setCreatedAt(rs.getTimestamp("created_at"));
        return resource;
    }
}
