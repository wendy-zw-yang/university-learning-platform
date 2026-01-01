<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.UserModel" %>
<%@ page import="com.ulp.bean.CourseModel" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ulp.service.UserService" %>
<%
    // 验证用户是否登录且为教师
    UserModel user = (UserModel) session.getAttribute("user");
    if (user == null || !"teacher".equals(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    // 获取课程列表
    List<CourseModel> courses = (List<CourseModel>) request.getAttribute("courses");

    // 创建UserService实例获取教师名
    UserService userService = new UserService();

    // 获取错误信息
    String errorMessage = (String) request.getAttribute("error");
    String successMessage = (String) request.getAttribute("success");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>课程资源管理 - 大学学习平台</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Microsoft YaHei', Arial, sans-serif;
            background-color: #f5f5f5;
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }

        h1 {
            color: #333;
            font-size: 28px;
        }

        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
            transition: all 0.3s;
        }

        .btn-primary {
            background-color: #007bff;
            color: white;
        }

        .btn-primary:hover {
            background-color: #0056b3;
        }

        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background-color: #5a6268;
        }

        .message {
            padding: 12px 20px;
            border-radius: 4px;
            margin-bottom: 20px;
        }

        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .course-list {
            margin-top: 30px;
        }

        /* 课程列表项整体样式 */
        .course-item {
            background: #f8f9fa;
            padding: 15px 16px;
            margin-bottom: 10px;
            border-radius: 4px;
            border-left: 4px solid #007bff;
            display: flex;
            justify-content: space-between;
            align-items: stretch;
            position: relative;
        }

        .course-item:hover {
            background: #e9ecef;
        }

        /* 课程链接内容块 */
        .course-link {
            text-decoration: none;
            color: #212529;
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        /* 右侧资源气泡和按钮 */
        .right-controls {
            display: flex;
            align-items: center;
            height: 100%;
            margin-left: 24px;
        }

        .course-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex: 1;
        }

        .course-header strong {
            font-size: 18px;
            font-weight: bold;
            color: #222;
        }

        .course-header span {
            font-size: 14px;
            color: #666;
            margin-left: 6px;
        }

        .course-desc {
            color: #888;
            font-size: 14px;
            margin-top: 4px;
            margin-left: 2px;
            font-weight: normal;
        }

        .no-courses {
            text-align: center;
            padding: 40px;
            color: #666;
            font-size: 16px;
        }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <div class="header">
        <h1>📚 课程资源管理</h1>
        <a href="${pageContext.request.contextPath}/teacher_homepage.jsp" class="btn btn-secondary">返回首页</a>
    </div>

    <%-- 显示消息 --%>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="message error"><%= errorMessage %></div>
    <% } %>

    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="message success"><%= successMessage %></div>
    <% } %>

    <div class="course-list">
        <% if (courses != null && !courses.isEmpty()) { %>
        <% for (CourseModel course : courses) {
            String teacherName = userService.getUsernameById(course.getTeacherId());
        %>
        <div class="course-item">
            <a href="${pageContext.request.contextPath}/teacher/edit_resources?courseId=<%= course.getId() %>" class="course-link">
                <div class="course-header">
                    <div>
                        <strong><%= course.getName() %></strong>
                        <span>ID: <%= course.getId() %></span>
                        <% if (teacherName != null) { %>
                        <span> | 教师: <%= teacherName %></span>
                        <% } %>
                    </div>
                </div>
                <% if (course.getDescription() != null && !course.getDescription().isEmpty()) { %>
                <div><%= course.getDescription() %></div>
                <% } %>
            </a>
            <div class="right-controls">
                <a href="${pageContext.request.contextPath}/teacher/edit_resources?courseId=<%= course.getId() %>" class="btn btn-primary">上传资源</a>
            </div>
        </div>
        <% } %>
        <% } else { %>
        <div class="no-courses">
            <p>您暂无教授的课程</p>
        </div>
        <% } %>
    </div>
</div>
</body>
</html>
