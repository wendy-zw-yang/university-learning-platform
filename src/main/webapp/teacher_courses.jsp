<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ulp.bean.CourseModel" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的课程权限管理 - 大学生学习平台</title>
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

        .btn-warning {
            background-color: #ffc107;
            color: #333;
        }

        .btn-warning:hover {
            background-color: #e0a800;
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
            padding: 12px;
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

        .actions-cell {
            display: flex;
            gap: 10px;
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
    <h1>我的课程权限管理</h1>

    <%-- 显示成功消息 --%>
    <% if (request.getParameter("success") != null) {
        String success = request.getParameter("success");
        String message = "";
        if ("update".equals(success)) message = "课程权限更新成功！";
    %>
    <div class="message success"><%= message %></div>
    <% } %>

    <%-- 显示错误消息 --%>
    <% if (request.getAttribute("error") != null) { %>
    <div class="message error"><%= request.getAttribute("error") %></div>
    <% } %>

    <div class="actions">
        <h2>我的课程列表</h2>
        <a href="${pageContext.request.contextPath}/teacher_homepage.jsp" class="btn btn-secondary">返回教师主页</a>
    </div>

    <%
        List<CourseModel> courses = (List<CourseModel>) request.getAttribute("courses");
        if (courses != null && !courses.isEmpty()) {
    %>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>课程名称</th>
            <th>学院</th>
            <th>可见性</th>
            <th>描述</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <% for (CourseModel course : courses) { %>
        <tr>
            <td><%= course.getId() %></td>
            <td><strong><%= course.getName() %></strong></td>
            <td><%= course.getCollege() != null && !course.getCollege().isEmpty() ? course.getCollege() : "-" %></td>
            <td>
                <% if ("all".equals(course.getVisibility())) { %>
                <span class="badge badge-all">全部学生</span>
                <% } else { %>
                <span class="badge badge-class">仅本班</span>
                <% } %>
            </td>
            <td>
                <%= course.getDescription() != null && course.getDescription().length() > 50
                        ? course.getDescription().substring(0, 50) + "..."
                        : (course.getDescription() != null ? course.getDescription() : "-") %>
            </td>
            <td>
                <div class="actions-cell">
                    <a href="${pageContext.request.contextPath}/teacher/courses?action=edit&id=<%= course.getId() %>"
                       class="btn btn-warning">编辑权限</a>
                </div>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <% } else { %>
    <div class="no-data">
        <p>您目前没有教授任何课程</p>
    </div>
    <% } %>
</div>
</body>
</html>
