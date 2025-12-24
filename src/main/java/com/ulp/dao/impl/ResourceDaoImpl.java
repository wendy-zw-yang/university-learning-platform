package com.ulp.dao.impl;

import com.ulp.dao.ResourceDao;
import com.ulp.bean.ResourceModel;
import com.ulp.util.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceDaoImpl implements ResourceDao {

    @Override
    public boolean addResource(ResourceModel resourceModel) {
        String sql = "INSERT INTO resources (title, description, file_path, course_id, uploader_id, download_count) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, resourceModel.getTitle());
            pstmt.setString(2, resourceModel.getDescription());
            pstmt.setString(3, resourceModel.getFilePath());
            pstmt.setInt(4, resourceModel.getCourseId());
            pstmt.setInt(5, resourceModel.getUploaderId());
            pstmt.setInt(6, resourceModel.getDownloadCount() != null ? resourceModel.getDownloadCount() : 0);
            
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
    public boolean updateResource(ResourceModel resourceModel) {
        String sql = "UPDATE resources SET title = ?, description = ?, file_path = ?, course_id = ?, uploader_id = ?, download_count = ? WHERE id = ?";
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, resourceModel.getTitle());
            pstmt.setString(2, resourceModel.getDescription());
            pstmt.setString(3, resourceModel.getFilePath());
            pstmt.setInt(4, resourceModel.getCourseId());
            pstmt.setInt(5, resourceModel.getUploaderId());
            pstmt.setInt(6, resourceModel.getDownloadCount() != null ? resourceModel.getDownloadCount() : 0);
            pstmt.setInt(7, resourceModel.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ResourceModel getResourceById(Integer id) {
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
    public List<ResourceModel> getAllResources() {
        String sql = "SELECT * FROM resources ORDER BY created_at DESC";
        List<ResourceModel> resourceModels = new ArrayList<>();
        
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                resourceModels.add(extractResourceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resourceModels;
    }

    @Override
    public List<ResourceModel> getResourcesByCourseId(Integer courseId) {
        String sql = "SELECT * FROM resources WHERE course_id = ? ORDER BY created_at DESC";
        List<ResourceModel> resourceModels = new ArrayList<>();
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                resourceModels.add(extractResourceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resourceModels;
    }

    @Override
    public List<ResourceModel> getResourcesByUploaderId(Integer uploaderId) {
        String sql = "SELECT * FROM resources WHERE uploader_id = ? ORDER BY created_at DESC";
        List<ResourceModel> resourceModels = new ArrayList<>();
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, uploaderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                resourceModels.add(extractResourceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resourceModels;
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
    public List<ResourceModel> searchResourcesByTitle(String title) {
        String sql = "SELECT * FROM resources WHERE title LIKE ? ORDER BY created_at DESC";
        List<ResourceModel> resourceModels = new ArrayList<>();
        
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                resourceModels.add(extractResourceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resourceModels;
    }

    /**
     * 从ResultSet中提取Resource对象
     * @param rs ResultSet对象
     * @return Resource对象
     * @throws SQLException SQL异常
     */
    private ResourceModel extractResourceFromResultSet(ResultSet rs) throws SQLException {
        ResourceModel resourceModel = new ResourceModel();
        resourceModel.setId(rs.getInt("id"));
        resourceModel.setTitle(rs.getString("title"));
        resourceModel.setDescription(rs.getString("description"));
        resourceModel.setFilePath(rs.getString("file_path"));
        resourceModel.setCourseId(rs.getInt("course_id"));
        resourceModel.setUploaderId(rs.getInt("uploader_id"));
        resourceModel.setDownloadCount(rs.getInt("download_count"));
        resourceModel.setCreatedAt(rs.getTimestamp("created_at"));
        return resourceModel;
    }
}
