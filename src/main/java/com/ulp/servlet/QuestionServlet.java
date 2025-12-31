package com.ulp.servlet;

import com.ulp.bean.AnswerModel;
import com.ulp.bean.CourseModel;
import com.ulp.bean.QuestionModel;
import com.ulp.bean.UserModel;
import com.ulp.dao.CourseDao;
import com.ulp.dao.impl.CourseDaoImpl;
import com.ulp.service.AnswerService;
import com.ulp.service.CourseService;
import com.ulp.service.QuestionService;
import com.ulp.service.StudentCourseService;
import com.ulp.service.NotificationService;
import com.ulp.util.DBHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
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
        UserModel user = (UserModel) request.getSession().getAttribute("user");
        if (user == null) {
            // 如果未登录，重定向到登录页面
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String courseIdParam = request.getParameter("courseId");
        String questionIdParam = request.getParameter("questionId");

        if ("answer".equals(action)) {
            // 教师回答问题操作
            if (user.getRole() != null && !"teacher".equals(user.getRole())) {
                request.setAttribute("error", "只有教师可以回答问题");
                request.getRequestDispatcher("/course_question.jsp").forward(request, response);
                return;
            }

            if (questionIdParam == null || questionIdParam.trim().isEmpty()) {
                request.setAttribute("error", "问题ID不能为空");
                request.getRequestDispatcher("/course_question.jsp").forward(request, response);
                return;
            }

            try {
                int questionId = Integer.parseInt(questionIdParam);

                // 这里应该获取问题信息并转发到回答页面
                // 为简化，我们直接转发到回答页面，页面会处理显示问题详情
                request.setAttribute("questionId", questionId);
                request.getRequestDispatcher("/answer_question.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "问题ID格式不正确");
                request.getRequestDispatcher("/course_question.jsp").forward(request, response);
            }
        } else {
            // 学生提问操作
            if (user.getRole() != null && !"student".equals(user.getRole())) {
                request.setAttribute("error", "只有学生可以提问");
                request.getRequestDispatcher("/student_courses.jsp").forward(request, response);
                return;
            }

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

                // 检查学生是否有权限提问该课程
                if (!isStudentAllowedToQuestion(user.getId(), course)) {
                    request.setAttribute("error", "您没有权限向该课程提问");
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
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserModel user = (UserModel) request.getSession().getAttribute("user");
        if (user == null) {
            // 如果未登录，重定向到登录页面
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 设置请求编码
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("answer".equals(action)) {
            // 教师回答问题操作
            if (user.getRole() != null && !"teacher".equals(user.getRole())) {
                request.setAttribute("error", "只有教师可以回答问题");
                request.getRequestDispatcher("/course_question.jsp").forward(request, response);
                return;
            }

            String questionIdParam = request.getParameter("questionId");
            String content = request.getParameter("content");

            if (questionIdParam == null || content == null) {
                request.setAttribute("error", "缺少必要参数");
                request.getRequestDispatcher("/answer_question.jsp").forward(request, response);
                return;
            }

            try {
                int questionId = Integer.parseInt(questionIdParam);

                // 获取问题信息以获取课程ID
                QuestionService questionService = new QuestionService();
                QuestionModel question = questionService.getQuestionById(questionId);
                if (question == null) {
                    request.setAttribute("error", "找不到指定的问题");
                    request.getRequestDispatcher("/answer_question.jsp").forward(request, response);
                    return;
                }

                // 获取上传的附件
                String attachmentPath = null;
                Part filePart = request.getPart("attachment");
                if (filePart != null && filePart.getSize() > 0) {
                    // 处理文件上传
                    String fileName = getFileName(filePart);
                    if (fileName != null && !fileName.trim().isEmpty()) {
                        String uploadFolder = getServletContext().getRealPath("/upload/answers");
                        File uploadDir = new File(uploadFolder);
                        if(!uploadDir.exists())
                            uploadDir.mkdirs();
                        attachmentPath = "/upload/answers/" + System.currentTimeMillis() + "_" + fileName;

                        // 保存文件到指定目录
                        String uploadPath = getServletContext().getRealPath(attachmentPath);
                        filePart.write(uploadPath);
                    }
                }

                // 创建AnswerModel对象
                AnswerModel answer = new AnswerModel();
                answer.setContent(content);
                answer.setAttachment(attachmentPath);
                answer.setQuestionId(questionId);
                answer.setTeacherId(user.getId());
                answer.setTeacherName(user.getUsername());

                // 保存回答到数据库
                AnswerService answerService = new AnswerService();
                boolean success = answerService.addAnswer(answer);

                if (success) {
                    // 创建回答通知给提问的学生
                    NotificationService notificationService = new NotificationService();
                    String message = "您在课程 " + question.getCourseId() + " 中的问题 \"" +
                            (question.getTitle().length() > 20 ? question.getTitle().substring(0, 20) + "..." : question.getTitle()) +
                            "\" 已被回答";
                    notificationService.createNotification(question.getStudentId(), "answer", message, questionId);

                    request.setAttribute("success", "回答提交成功！");
                } else {
                    request.setAttribute("error", "回答提交失败，请重试");
                }

                // 重定向回问题所在课程的页面
                response.sendRedirect(request.getContextPath() + "/teacher/questions?courseId=" + question.getCourseId());
            } catch (NumberFormatException e) {
                request.setAttribute("error", "问题ID格式不正确");
                request.getRequestDispatcher("/answer_question.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "系统错误：" + e.getMessage());
                request.getRequestDispatcher("/answer_question.jsp").forward(request, response);
            }
        } else {
            // 学生提问操作
            if (user.getRole() != null && !"student".equals(user.getRole())) {
                request.setAttribute("error", "只有学生可以提问");
                request.getRequestDispatcher("/student_courses.jsp").forward(request, response);
                return;
            }

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

                // 获取课程信息
                CourseModel course = new CourseService().getCourseById(courseId);
                if (course == null) {
                    request.setAttribute("error", "找不到指定的课程");
                    request.getRequestDispatcher("/student_courses.jsp").forward(request, response);
                    return;
                }

                // 检查学生是否有权限提问该课程
                if (!isStudentAllowedToQuestion(user.getId(), course)) {
                    request.setAttribute("error", "您没有权限向该课程提问");
                    request.getRequestDispatcher("/student_courses.jsp").forward(request, response);
                    return;
                }

                // 获取上传的附件
                String attachmentPath = null;
                Part filePart = request.getPart("attachment");
                if (filePart != null && filePart.getSize() > 0) {
                    // 处理文件上传
                    String fileName = getFileName(filePart);
                    if (fileName != null && !fileName.trim().isEmpty()) {
                        // 这里应该将文件保存到指定位置，并返回文件路径
                        // 为简化，这里直接使用原始文件名，实际应用中应生成唯一文件名
                        String uploadFolder = getServletContext().getRealPath("/upload/questions");
                        File uploadDir = new File(uploadFolder);
                        if(!uploadDir.exists())
                            uploadDir.mkdirs();
                        attachmentPath = "/upload/questions/" + System.currentTimeMillis() + "_" + fileName;

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
                question.setStudentId(user.getId());
                question.setCreatedAt(new Timestamp(System.currentTimeMillis()));

                // 保存问题到数据库
                QuestionService questionService = new QuestionService();
                boolean success = questionService.addQuestion(question);

                if (success) {
                    // 保存成功，重定向到成功页面或课程页面
                    request.setAttribute("success", "问题提交成功！");
                } else {
                    request.setAttribute("error", "问题提交失败，请重试");
                }
                request.setAttribute("course", new CourseService().getCourseById(courseId));
                request.getRequestDispatcher("/ask_question.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "课程ID格式不正确");
                request.getRequestDispatcher("/ask_question.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "系统错误：" + e.getMessage());
                request.getRequestDispatcher("/ask_question.jsp").forward(request, response);
            }
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

    /**
     * 检查学生是否有权限向指定课程提问
     * @param studentId 学生ID
     * @param course 课程对象
     * @return 是否有权限
     */
    private boolean isStudentAllowedToQuestion(int studentId, CourseModel course) {
        if ("all".equals(course.getVisibility())) {
            // 如果课程对所有学生可见，则允许提问
            return true;
        } else if ("class_only".equals(course.getVisibility())) {
            // 如果课程仅对本班学生可见，则检查学生是否已选该课程
            StudentCourseService studentCourseService = new StudentCourseService();
            return studentCourseService.isStudentEnrolled(studentId, course.getId());
        }
        return false;
    }
}