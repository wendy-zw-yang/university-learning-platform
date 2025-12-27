package com.ulp.servlet;

import com.ulp.bean.CourseModel;
import com.ulp.bean.QuestionModel;
import com.ulp.bean.UserModel;
import com.ulp.dao.CourseDao;
import com.ulp.dao.impl.CourseDaoImpl;
import com.ulp.service.CourseService;
import com.ulp.util.DBHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@WebServlet("/questions")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class QuestionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 获取当前登录学生，这里假设已登录学生user存储在session中
        UserModel student= (UserModel) request.getSession().getAttribute("user");
        if (student == null) {
            // 如果未登录，重定向到登录页面
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 获取课程ID参数
        String courseIdParam = request.getParameter("courseId");
        if (courseIdParam == null || courseIdParam.trim().isEmpty()) {
            request.setAttribute("error", "课程ID不能为空");
            request.getRequestDispatcher("/student_courses.jsp").forward(request, response);
            return;
        }

        try {
            int courseId = Integer.parseInt(courseIdParam);
            
            // 获取课程信息
            CourseModel course = new CourseService().getCourseById(courseId);
            
            if (course == null) {
                request.setAttribute("error", "找不到指定的课程");
                request.getRequestDispatcher("/student_courses.jsp").forward(request, response);
                return;
            }

            // 设置课程信息到request属性中
            request.setAttribute("course", course);
            
            // 转发到提问页面
            request.getRequestDispatcher("/ask_question.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "课程ID格式不正确");
            request.getRequestDispatcher("/student_courses.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 获取当前登录学生ID
        Integer studentId = (Integer) request.getSession().getAttribute("userId");
        if (studentId == null) {
            // 如果未登录，重定向到登录页面
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 设置请求编码
        request.setCharacterEncoding("UTF-8");

        // 获取表单数据
        String courseIdParam = request.getParameter("courseId");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        if (courseIdParam == null || title == null || content == null) {
            request.setAttribute("error", "缺少必要参数");
            request.getRequestDispatcher("/ask_question.jsp").forward(request, response);
            return;
        }

        try {
            int courseId = Integer.parseInt(courseIdParam);
            
            // 获取上传的附件
            String attachmentPath = null;
            Part filePart = request.getPart("attachment");
            if (filePart != null && filePart.getSize() > 0) {
                // 处理文件上传
                String fileName = getFileName(filePart);
                if (fileName != null && !fileName.trim().isEmpty()) {
                    // 这里应该将文件保存到指定位置，并返回文件路径
                    // 为简化，这里直接使用原始文件名，实际应用中应生成唯一文件名
                    attachmentPath = "/uploads/questions/" + System.currentTimeMillis() + "_" + fileName;
                    
                    // 保存文件到指定目录
                    String uploadPath = getServletContext().getRealPath(attachmentPath);
                    filePart.write(uploadPath);
                }
            }

            // 创建QuestionModel对象
            QuestionModel question = new QuestionModel();
            question.setTitle(title);
            question.setContent(content);
            question.setAttachment(attachmentPath);
            question.setCourseId(courseId);
            question.setStudentId(studentId);
            question.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // 保存问题到数据库
            boolean success = saveQuestion(question);
            
            if (success) {
                // 保存成功，重定向到成功页面或课程页面
                request.setAttribute("success", "问题提交成功！");
                request.getRequestDispatcher("/ask_question.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "问题提交失败，请重试");
                request.getRequestDispatcher("/ask_question.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "课程ID格式不正确");
            request.getRequestDispatcher("/ask_question.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "系统错误：" + e.getMessage());
            request.getRequestDispatcher("/ask_question.jsp").forward(request, response);
        }
    }
    

    // 保存问题到数据库
    private boolean saveQuestion(QuestionModel question) {
        String sql = "INSERT INTO questions (title, content, attachment, course_id, student_id, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, question.getTitle());
            pstmt.setString(2, question.getContent());
            pstmt.setString(3, question.getAttachment());
            pstmt.setInt(4, question.getCourseId());
            pstmt.setInt(5, question.getStudentId());
            pstmt.setTimestamp(6, question.getCreatedAt());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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