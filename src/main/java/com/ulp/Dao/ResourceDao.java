package com.ulp.Dao;

import com.ulp.bean.Resource;
import java.util.List;

public interface ResourceDao {
    // 添加资源
    boolean addResource(Resource resource);
    // 删除资源
    boolean deleteResourceById(Integer id);
    // 更新资源信息
    boolean updateResource(Resource resource);
    // 根据 Id 查询资源
    Resource getResourceById(Integer id);
    // 获取所有资源
    List<Resource> getAllResources();
    // 根据 课程Id 获取资源列表
    List<Resource> getResourcesByCourseId(Integer courseId);
    // 根据 上传者ID 获取资源列表
    List<Resource> getResourcesByUploaderId(Integer uploaderId);
    // 增加资源下载次数
    boolean incrementDownloadCount(Integer id);
    // 根据标题模糊查询资源
    List<Resource> searchResourcesByTitle(String title);
}
