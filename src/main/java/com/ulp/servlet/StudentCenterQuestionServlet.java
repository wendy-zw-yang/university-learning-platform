package com.ulp.servlet;

import com.ulp.bean.QuestionModel;
import com.ulp.bean.UserModel;
import com.ulp.service.QuestionService;

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

@WebServlet("/student/center/question")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,       // 10MB
        maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class StudentCenterQuestionServlet extends HttpServlet {
    private QuestionService questionService;

    public StudentCenterQuestionServlet() {
        this.questionService = new QuestionService();
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
        String questionIdParam = request.getParameter("questionId");

        if ("edit".equals(action) && questionIdParam != null) {
            // 编辑问题 - 显示编辑表单
            try {
                Integer questionId = Integer.parseInt(questionIdParam);
                QuestionModel question = questionService.getQuestionById(questionId);

                // 验证问题是否属于当前用户
                if (question != null && question.getStudentId() == user.getId()) {
                    request.setAttribute("question", question);
                    request.getRequestDispatcher("/student_center_edit_question.jsp").forward(request, response);
                    return;
                } else {
                    request.setAttribute("error", "找不到指定的问题或您没有权限编辑此问题！");
                    request.getRequestDispatcher("/student_center.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "无效的问题ID！");
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
            // 删除问题
            String questionIdParam = request.getParameter("questionId");
            if (questionIdParam != null) {
                try {
                    Integer questionId = Integer.parseInt(questionIdParam);
                    QuestionModel question = questionService.getQuestionById(questionId);

                    // 验证问题是否属于当前用户
                    if (question != null && question.getStudentId() == user.getId()) {
                        boolean success = questionService.deleteQuestionById(questionId);
                        if (success) {
                            request.setAttribute("success", "问题删除成功！");
                        } else {
                            request.setAttribute("error", "删除问题失败！");
                        }
                    } else {
                        request.setAttribute("error", "您没有权限删除此问题！");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "无效的问题ID！");
                }
            } else {
                request.setAttribute("error", "无效的问题ID！");
            }
        } else if ("update".equals(action)) {
            // 更新问题信息
            String questionIdParam = request.getParameter("questionId");
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            if (questionIdParam != null && title != null && !title.trim().isEmpty()) {
                try {
                    Integer questionId = Integer.parseInt(questionIdParam);
                    QuestionModel question = questionService.getQuestionById(questionId);

                    // 验证问题是否属于当前用户
                    if (question != null && question.getStudentId() == user.getId()) {
                        question.setTitle(title);
                        question.setContent(content);

                        // 获取上传的附件
                        String newAttachmentPath = null;
                        try {
                            Part filePart = request.getPart("newAttachment");
                            if (filePart != null && filePart.getSize() > 0) {
                                // 处理文件上传
                                String fileName = getFileName(filePart);
                                if (fileName != null && !fileName.trim().isEmpty()) {
                                    String uploadFolder = getServletContext().getRealPath("/upload/questions");
                                    File uploadDir = new File(uploadFolder);
                                    if(!uploadDir.exists())
                                        uploadDir.mkdirs();
                                    newAttachmentPath = "/upload/questions/" + System.currentTimeMillis() + "_" + fileName;

                                    // 保存文件到指定目录
                                    String uploadPath = getServletContext().getRealPath(newAttachmentPath);
                                    System.out.println("Write To: " + uploadPath);
                                    filePart.write(uploadPath);

                                    // 如果有新附件上传，则删除旧附件
                                    if (question.getAttachment() != null && !question.getAttachment().isEmpty()) {
                                        String oldFilePath = getServletContext().getRealPath(question.getAttachment());
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

                        // 更新问题信息
                        if (newAttachmentPath != null) {
                            question.setAttachment(newAttachmentPath);
                            // 同时更新数据库中的附件字段
                            boolean attachmentUpdated = questionService.updateQuestionAttachment(questionId, newAttachmentPath);
                            if (!attachmentUpdated) {
                                request.setAttribute("error", "更新附件失败！");
                                request.getRequestDispatcher("/student_center.jsp").forward(request, response);
                                return;
                            }
                        }

                        boolean success = questionService.updateQuestionContent(questionId, content);
                        if (success) {
                            request.setAttribute("success", "问题信息更新成功！");
                        } else {
                            request.setAttribute("error", "更新问题信息失败！");
                        }
                    } else {
                        request.setAttribute("error", "您没有权限更新此问题！");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "无效的问题ID！");
                }
            } else {
                request.setAttribute("error", "请输入完整的问题信息！");
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
