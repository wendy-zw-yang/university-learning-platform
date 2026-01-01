package com.ulp.servlet;

import com.ulp.bean.CourseModel;
import com.ulp.bean.ResourceModel;
import com.ulp.bean.UserModel;
import com.ulp.service.CourseService;
import com.ulp.service.ResourceService;
import com.ulp.service.StudentCourseService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 100,      // 100MB
        maxRequestSize = 1024 * 1024 * 100    // 100MB
)
@WebServlet("/student/resource")
public class StudentResourceServlet extends HttpServlet {
    private ResourceService resourceService;
    private CourseService courseService;
    private StudentCourseService studentCourseService;

    public StudentResourceServlet() {
        this.resourceService = new ResourceService();
        this.courseService = new CourseService();
        this.studentCourseService = new StudentCourseService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 验证用户权限
        HttpSession session = request.getSession();
        Object userObj = session.getAttribute("user");

        if (userObj == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 检查用户角色
        try {
            String role = (String) userObj.getClass().getMethod("getRole").invoke(userObj);
            if (!"student".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserModel user = (UserModel) userObj;
        int studentId = user.getId();

        // 获取所有可见的课程（权限为"all"或学生已选的课程）
        List<CourseModel> allCourses = courseService.getAllCourses();
        List<Integer> enrolledCourseIds = studentCourseService.getEnrolledCourseIds(studentId);
        List<CourseModel> visibleCourses = getVisibleCourses(allCourses, enrolledCourseIds);

        // 获取当前选中的课程ID
        String courseIdParam = request.getParameter("courseId");
        Integer selectedCourseId = null;
        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                selectedCourseId = Integer.parseInt(courseIdParam);
            } catch (NumberFormatException e) {
                // 如果参数无效，忽略
            }
        }

        // 获取搜索参数
        String searchQuery = request.getParameter("search");

        // 获取所有资源
        List<ResourceModel> allResources = resourceService.getAllResources();

        // 如果有搜索参数，则过滤资源
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            List<ResourceModel> filteredResources = new java.util.ArrayList<>();
            for (ResourceModel resource : allResources) {
                if (resource.getTitle().toLowerCase().contains(searchQuery.toLowerCase())) {
                    filteredResources.add(resource);
                }
            }
            allResources = filteredResources;
        }

        request.setAttribute("courses", visibleCourses);
        request.setAttribute("resources", allResources);
        request.setAttribute("selectedCourseId", selectedCourseId);

        request.getRequestDispatcher("/student_resource.jsp").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 验证用户权限
        HttpSession session = request.getSession();
        Object userObj = session.getAttribute("user");

        if (userObj == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 检查用户角色
        try {
            String role = (String) userObj.getClass().getMethod("getRole").invoke(userObj);
            if (!"student".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        if ("upload".equals(action)) {
            handleUploadResource(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/student_homepage.jsp");
        }
    }

    private void handleUploadResource(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 获取表单参数
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String courseIdParam = request.getParameter("courseId");
            String uploaderIdParam = request.getParameter("uploaderId");

            // 验证参数
            if (title == null || title.trim().isEmpty() ||
                    courseIdParam == null || uploaderIdParam == null) {
                request.setAttribute("error", "请填写完整的资源信息！");
                forwardToUploadPage(request, response);
                return;
            }

            int courseId;
            int uploaderId;
            try {
                courseId = Integer.parseInt(courseIdParam);
                uploaderId = Integer.parseInt(uploaderIdParam);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "无效的课程ID或上传者ID！");
                forwardToUploadPage(request, response);
                return;
            }

            // 验证上传者ID与当前登录用户是否一致
            HttpSession session = request.getSession();
            UserModel currentUser = (UserModel) session.getAttribute("user");
            if (currentUser == null || currentUser.getId() != uploaderId) {
                request.setAttribute("error", "上传者ID与当前用户不匹配！");
                forwardToUploadPage(request, response);
                return;
            }

            // 获取上传的文件
            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                request.setAttribute("error", "请选择要上传的文件！");
                forwardToUploadPage(request, response);
                return;
            }

            // 验证文件类型
            String fileName = getFileName(filePart);
            if (fileName == null || fileName.isEmpty()) {
                request.setAttribute("error", "请选择有效的文件！");
                forwardToUploadPage(request, response);
                return;
            }

            String fileExtension = getFileExtension(fileName).toLowerCase();
            String[] allowedExtensions = {".pdf", ".doc", ".docx", ".ppt", ".pptx", ".jpg", ".jpeg", ".png", ".gif", ".txt", ".zip"};
            boolean isAllowed = false;
            for (String ext : allowedExtensions) {
                if (ext.equals(fileExtension)) {
                    isAllowed = true;
                    break;
                }
            }

            if (!isAllowed) {
                request.setAttribute("error", "不支持的文件类型！请上传PDF、Word、PPT、图片、文本或ZIP文件。");
                forwardToUploadPage(request, response);
                return;
            }

            // 验证文件大小（100MB限制）
            if (filePart.getSize() > 1024 * 1024 * 100) { // 100MB
                request.setAttribute("error", "文件大小不能超过100MB！");
                forwardToUploadPage(request, response);
                return;
            }

            // 生成文件保存路径
            String uploadPath = getServletContext().getRealPath("/uploads");
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 确保文件名唯一
            String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
            String filePath = "/uploads/" + uniqueFileName;

            // 保存文件
            filePart.write(uploadPath + File.separator + uniqueFileName);

            // 创建资源对象
            ResourceModel resource = new ResourceModel();
            resource.setTitle(title);
            resource.setDescription(description);
            resource.setFilePath("/uploads/" + uniqueFileName); // 保存完整路径，以/开头
            resource.setCourseId(courseId);
            resource.setUploaderId(uploaderId);
            resource.setDownloadCount(0);
            resource.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // 保存到数据库
            boolean success = resourceService.addResource(resource);

            if (success) {
                request.setAttribute("success", "资源上传成功！");
            } else {
                request.setAttribute("error", "资源上传失败！");
                // 删除已上传的文件
                new File(uploadPath + File.separator + uniqueFileName).delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "上传过程中发生错误：" + e.getMessage());
        }

        forwardToUploadPage(request, response);
    }

    private void forwardToUploadPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 获取学生已选的课程列表
        HttpSession session = request.getSession();
        Object userObj = session.getAttribute("user");

        if (userObj != null) {
            try {
                UserModel user = (UserModel) userObj;
                List<Integer> enrolledCourseIds = studentCourseService.getEnrolledCourseIds(user.getId());
                List<CourseModel> allCourses = courseService.getAllCourses();
                List<CourseModel> visibleCourses = getVisibleCourses(allCourses, enrolledCourseIds);
                request.setAttribute("courses", visibleCourses);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        request.getRequestDispatcher("/student_edit_resource.jsp").forward(request, response);
    }

    private List<CourseModel> getVisibleCourses(List<CourseModel> allCourses, List<Integer> enrolledCourseIds) {
        List<CourseModel> visibleCourses = new java.util.ArrayList<>();
        for (CourseModel course : allCourses) {
            // 如果课程权限为"all"或学生已选该课程，则该课程对当前学生可见
            if ("all".equals(course.getVisibility()) || enrolledCourseIds.contains(course.getId())) {
                visibleCourses.add(course);
            }
        }
        return visibleCourses;
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition != null) {
            for (String content : contentDisposition.split(";")) {
                if (content.trim().startsWith("filename")) {
                    return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
                }
            }
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot > 0) {
            return fileName.substring(lastIndexOfDot);
        }
        return "";
    }
}
