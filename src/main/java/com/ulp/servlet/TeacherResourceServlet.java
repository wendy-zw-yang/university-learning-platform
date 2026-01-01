package com.ulp.servlet;

import com.ulp.bean.CourseModel;
import com.ulp.bean.ResourceModel;
import com.ulp.bean.UserModel;
import com.ulp.service.CourseService;
import com.ulp.service.ResourceService;
import com.ulp.service.TeacherService;

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
@WebServlet("/teacher/resource")
public class TeacherResourceServlet extends HttpServlet {
    private final ResourceService resourceService;
    private final CourseService courseService;
    private final TeacherService teacherService;

    public TeacherResourceServlet() {
        this.resourceService = new ResourceService();
        this.courseService = new CourseService();
        this.teacherService = new TeacherService();
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
            if (!"teacher".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 获取当前教师的课程列表
        UserModel user = (UserModel) userObj;
        List<com.ulp.bean.CourseModel> courses = teacherService.getCoursesByTeacherId(user.getId());

        request.setAttribute("courses", courses);
        request.getRequestDispatcher("/teacher_upload_resource.jsp").forward(request, response);
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
            if (!"teacher".equals(role)) {
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
            response.sendRedirect(request.getContextPath() + "/teacher_homepage.jsp");
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

            // 验证教师是否有权限上传到该课程（即该课程是否属于当前教师）
            List<com.ulp.bean.CourseModel> teacherCourses = teacherService.getCoursesByTeacherId(uploaderId);
            boolean hasPermission = false;
            for (com.ulp.bean.CourseModel course : teacherCourses) {
                if (course.getId() == courseId) {
                    hasPermission = true;
                    break;
                }
            }

            if (!hasPermission) {
                request.setAttribute("error", "您没有权限上传资源到此课程！只能上传到您教授的课程。");
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
        // 获取教师的课程列表
        HttpSession session = request.getSession();
        Object userObj = session.getAttribute("user");

        if (userObj != null) {
            try {
                UserModel user = (UserModel) userObj;
                List<com.ulp.bean.CourseModel> courses = teacherService.getCoursesByTeacherId(user.getId());
                request.setAttribute("courses", courses);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        request.getRequestDispatcher("/teacher_upload_resource.jsp").forward(request, response);
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
