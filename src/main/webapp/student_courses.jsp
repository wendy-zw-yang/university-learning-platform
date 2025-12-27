<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ulp.bean.CourseModel" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>课程管理 - 大学生学习平台</title>
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
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        h1 {
            color: #333;
            margin-bottom: 30px;
            font-size: 28px;
        }

        .actions {
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
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

        .btn-success {
            background-color: #28a745;
            color: white;
        }

        .btn-success:hover {
            background-color: #218838;
        }

        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background-color: #5a6268;
        }

        .btn-outline {
            background-color: transparent;
            border: 1px solid #007bff;
            color: #007bff;
        }

        .btn-outline:hover {
            background-color: #007bff;
            color: white;
        }

        .btn-gray {
            background-color: #6c757d;
            color: white;
        }

        .btn-gray:hover {
            background-color: #5a6268;
        }

        .message {
            padding: 12px 20px;
            border-radius: 4px;
            margin-bottom: 20px;
        }

        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #333;
        }

        tr:hover {
            background-color: #f8f9fa;
        }

        .course-name {
            font-weight: 500;
            color: #333;
        }

        .course-info {
            color: #666;
            font-size: 14px;
        }

        .actions-cell {
            text-align: left;
        }

        .no-data {
            text-align: center;
            padding: 40px;
            color: #666;
            font-size: 16px;
        }

        .badge {
            padding: 4px 8px;
            border-radius: 3px;
            font-size: 12px;
            font-weight: 500;
        }

        .badge-all {
            background-color: #28a745;
            color: white;
        }

        .badge-class {
            background-color: #17a2b8;
            color: white;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>课程管理</h1>

    <%-- 显示成功消息 --%>
    <% if (request.getParameter("success") != null) {
        String success = request.getParameter("success");
        String message = "";
        if ("enroll".equals(success)) message = "课程选择成功！";
        else if ("unenroll".equals(success)) message = "课程退选成功！";
    %>
    <div class="message success"><%= message %></div>
    <% } %>

    <%-- 显示错误消息 --%>
    <% if (request.getParameter("error") != null) {
        String error = request.getParameter("error");
        String message = "";
        if ("enroll".equals(error)) message = "选课失败！";
        else if ("unenroll".equals(error)) message = "退课失败！";
        else if ("invalid".equals(error)) message = "无效的课程ID！";
    %>
    <div class="message error"><%= message %></div>
    <% } %>

    <div class="actions">
        <h2>所有课程</h2>
        <a href="${pageContext.request.contextPath}/student_homepage.jsp" class="btn btn-secondary">返回学生首页</a>
    </div>

    <%
        List<CourseModel> courses = (List<CourseModel>) request.getAttribute("courses");
        List<Integer> enrolledCourseIds = (List<Integer>) request.getAttribute("enrolledCourseIds");
        if (courses != null && !courses.isEmpty()) {
    %>
    <table>
        <thead>
        <tr>
            <th>课程ID</th>
            <th>课程名称</th>
            <th>教师</th>
            <th>学院</th>
            <th>可见性</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <% for (CourseModel course : courses) {
            boolean isEnrolled = enrolledCourseIds != null && enrolledCourseIds.contains(course.getId());
        %>
        <tr>
            <td><%= course.getId() %></td>
            <td>
                <div class="course-name"><%= course.getName() %></div>
                <div class="course-info"><%= course.getDescription() != null && !course.getDescription().isEmpty() ? course.getDescription().substring(0, Math.min(course.getDescription().length(), 50)) + "..." : "-" %></div>
            </td>
            <td>
                <% if (course.getTeacherId() != null) { %>
                <span class="teacher-info">教师ID: <%= course.getTeacherId() %></span>
                <% } else { %>
                <span class="teacher-info">暂无教师</span>
                <% } %>
            </td>
            <td><%= course.getCollege() != null && !course.getCollege().isEmpty() ? course.getCollege() : "-" %></td>
            <td>
                <% if ("all".equals(course.getVisibility())) { %>
                <span class="badge badge-all">全部学生</span>
                <% } else { %>
                <span class="badge badge-class">仅本班</span>
                <% } %>
            </td>
            <td class="actions-cell">
                <% if (isEnrolled) { %>
                <form method="post" action="${pageContext.request.contextPath}/student/courses" style="display: inline;">
                    <input type="hidden" name="action" value="unenroll">
                    <input type="hidden" name="courseId" value="<%= course.getId() %>">
                    <button type="submit" class="btn btn-gray">取消课程</button>
                    <a href="${pageContext.request.contextPath}/questions?courseId=<%= course.getId() %>" class="btn btn-success">提问</a>

                </form>
                <% } else { %>
                <form method="post" action="${pageContext.request.contextPath}/student/courses" style="display: inline;">
                    <input type="hidden" name="action" value="enroll">
                    <input type="hidden" name="courseId" value="<%= course.getId() %>">
                    <button type="submit" class="btn btn-primary">选择课程</button>
                </form>
                <% } %>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <% } else { %>
    <div class="no-data">
        <p>暂无课程数据</p>
    </div>
    <% } %>
</div>
</body>
</html>
