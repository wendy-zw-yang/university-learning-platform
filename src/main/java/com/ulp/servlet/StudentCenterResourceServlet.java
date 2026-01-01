package com.ulp.servlet;

import com.ulp.bean.ResourceModel;
import com.ulp.bean.UserModel;
import com.ulp.service.ResourceService;

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
import java.util.List;

@WebServlet("/student/center/resource")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,       // 10MB
        maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class StudentCenterResourceServlet extends HttpServlet {
    private ResourceService resourceService;

    public StudentCenterResourceServlet() {
        this.resourceService = new ResourceService();
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
        String action = request.getParameter("action");
        String resourceIdParam = request.getParameter("id");

        if ("edit".equals(action) && resourceIdParam != null) {
            // 编辑资源 - 显示编辑表单
            try {
                Integer resourceId = Integer.parseInt(resourceIdParam);
                ResourceModel resource = resourceService.getResourceById(resourceId);

                // 验证资源是否属于当前用户
                if (resource != null && resource.getUploaderId().equals(user.getId())) {
                    request.setAttribute("resource", resource);
                    request.getRequestDispatcher("/student_center_edit_resource.jsp").forward(request, response);
                    return;
                } else {
                    request.setAttribute("error", "找不到指定的资源或您没有权限编辑此资源！");
                    request.getRequestDispatcher("/student_center.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "无效的资源ID！");
                request.getRequestDispatcher("/student_center.jsp").forward(request, response);
                return;
            }
        }

        // 默认重定向到个人中心
        response.sendRedirect(request.getContextPath() + "/student_center.jsp");
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

        UserModel user = (UserModel) userObj;
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            // 删除资源
            String idParam = request.getParameter("id");
            if (idParam != null) {
                try {
                    Integer id = Integer.parseInt(idParam);
                    ResourceModel resource = resourceService.getResourceById(id);

                    // 验证资源是否属于当前用户
                    if (resource != null && resource.getUploaderId().equals(user.getId())) {
                        boolean success = resourceService.deleteResource(id);
                        if (success) {
                            request.setAttribute("success", "资源删除成功！");
                        } else {
                            request.setAttribute("error", "删除资源失败！");
                        }
                    } else {
                        request.setAttribute("error", "您没有权限删除此资源！");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "无效的资源ID！");
                }
            } else {
                request.setAttribute("error", "无效的资源ID！");
            }
        } else if ("update".equals(action)) {
            // 更新资源信息
            String idParam = request.getParameter("id");
            String title = request.getParameter("title");
            String description = request.getParameter("description");

            if (idParam != null && title != null && !title.trim().isEmpty()) {
                try {
                    Integer id = Integer.parseInt(idParam);
                    ResourceModel resource = resourceService.getResourceById(id);

                    // 验证资源是否属于当前用户
                    if (resource != null && resource.getUploaderId().equals(user.getId())) {
                        resource.setTitle(title);
                        resource.setDescription(description);

                        // 获取上传的附件
                        String newFilePath = null;
                        try {
                            Part filePart = request.getPart("newFile");
                            if (filePart != null && filePart.getSize() > 0) {
                                // 处理文件上传
                                String fileName = getFileName(filePart);
                                if (fileName != null && !fileName.trim().isEmpty()) {
                                    String uploadFolder = getServletContext().getRealPath("/uploads/resources");
                                    File uploadDir = new File(uploadFolder);
                                    if(!uploadDir.exists())
                                        uploadDir.mkdirs();
                                    newFilePath = "/uploads/resources/" + System.currentTimeMillis() + "_" + fileName;

                                    // 保存文件到指定目录
                                    String uploadPath = getServletContext().getRealPath(newFilePath);
                                    System.out.println("Write To: " + uploadPath);
                                    filePart.write(uploadPath);

                                    // 如果有新附件上传，则删除旧附件
                                    if (resource.getFilePath() != null && !resource.getFilePath().isEmpty()) {
                                        String oldFilePath = getServletContext().getRealPath(resource.getFilePath());
                                        File oldFile = new File(oldFilePath);
                                        if (oldFile.exists()) {
                                            oldFile.delete();
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            // 即使附件上传失败，也继续更新内容
                        }

                        // 更新资源信息
                        if (newFilePath != null) {
                            resource.setFilePath(newFilePath);
                        }

                        boolean success = resourceService.updateResource(resource);
                        if (success) {
                            request.setAttribute("success", "资源信息更新成功！");
                        } else {
                            request.setAttribute("error", "更新资源信息失败！");
                        }
                    } else {
                        request.setAttribute("error", "您没有权限更新此资源！");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "无效的资源ID！");
                }
            } else {
                request.setAttribute("error", "请输入完整的资源信息！");
            }
        }

        // 重定向到个人中心页面
        response.sendRedirect(request.getContextPath() + "/student_center.jsp");
    }

    // 获取上传文件的文件名
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
}
