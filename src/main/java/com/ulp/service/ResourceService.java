package com.ulp.service;

import com.ulp.dao.ResourceDao;
import com.ulp.dao.impl.ResourceDaoImpl;
import com.ulp.bean.ResourceModel;

import java.util.List;

public class ResourceService {
    private ResourceDao resourceDao;

    public ResourceService() {
        this.resourceDao = new ResourceDaoImpl();
    }

    // 添加资源
    public boolean addResource(ResourceModel resourceModel) {
        if (resourceModel == null || resourceModel.getTitle() == null || resourceModel.getFilePath() == null) {
            return false;
        }
        return resourceDao.addResource(resourceModel);
    }

    // 删除资源
    public boolean deleteResource(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return resourceDao.deleteResourceById(id);
    }

    // 更新资源信息
    public boolean updateResource(ResourceModel resourceModel) {
        if (resourceModel == null || resourceModel.getId() == null || resourceModel.getId() <= 0) {
            return false;
        }
        return resourceDao.updateResource(resourceModel);
    }

    // 根据 Id 获取资源
    public ResourceModel getResourceById(Integer id) {
        if (id == null || id <= 0) {
            return null;
        }
        return resourceDao.getResourceById(id);
    }

    // 获取所有资源
    public List<ResourceModel> getAllResources() {
        return resourceDao.getAllResources();
    }

    // 根据 课程Id 获取资源
    public List<ResourceModel> getResourcesByCourseId(Integer courseId) {
        if (courseId == null || courseId <= 0) {
            return null;
        }
        return resourceDao.getResourcesByCourseId(courseId);
    }

    // 根据上传者ID获取资源列表
    public List<ResourceModel> getResourcesByUploaderId(Integer uploaderId) {
        if (uploaderId == null || uploaderId <= 0) {
            return null;
        }
        return resourceDao.getResourcesByUploaderId(uploaderId);
    }

    // 增加资源下载次数
    public boolean incrementDownloadCount(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return resourceDao.incrementDownloadCount(id);
    }

    // 根据标题搜索资源
    public List<ResourceModel> searchResourcesByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return getAllResources();
        }
        return resourceDao.searchResourcesByTitle(title);
    }
}
