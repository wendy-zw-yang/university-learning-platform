<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.CourseModel" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>编辑课程权限 - 大学生学习平台</title>
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
            max-width: 800px;
            margin: 0 auto;
            background: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        h1 {
            color: #333;
            margin-bottom: 30px;
            font-size: 28px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 500;
        }

        .required {
            color: #dc3545;
        }

        input[type="text"],
        textarea,
        select {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            font-family: inherit;
            transition: border-color 0.3s;
        }

        input[type="text"]:focus,
        textarea:focus,
        select:focus {
            outline: none;
            border-color: #007bff;
        }

        textarea {
            resize: vertical;
            min-height: 100px;
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

        .info {
            background-color: #d1ecf1;
            color: #0c5460;
            border: 1px solid #bee5eb;
            padding: 12px;
            border-radius: 4px;
            margin-bottom: 20px;
        }

        .form-actions {
            display: flex;
            gap: 10px;
            margin-top: 30px;
        }

        .btn {
            padding: 12px 30px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            text-decoration: none;
            display: inline-block;
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

        .help-text {
            font-size: 12px;
            color: #666;
            margin-top: 5px;
        }
    </style>
</head>
<body>
<div class="container">
    <%
        CourseModel course = (CourseModel) request.getAttribute("course");
    %>

    <h1>编辑课程权限</h1>

    <%-- 显示错误消息 --%>
    <% if (request.getAttribute("error") != null) { %>
    <div class="message error"><%= request.getAttribute("error") %></div>
    <% } %>

    <% if (course != null) { %>
    <div class="info">
        <strong>课程名称：</strong><%= course.getName() %><br>
        <strong>课程ID：</strong><%= course.getId() %><br>
        <strong>当前可见性：</strong>
        <% if ("all".equals(course.getVisibility())) { %>
        <span class="badge" style="background-color: #28a745; color: white;">全部学生</span>
        <% } else { %>
        <span class="badge" style="background-color: #17a2b8; color: white;">仅本班</span>
        <% } %>
    </div>

    <form method="post" action="${pageContext.request.contextPath}/teacher/courses">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="id" value="<%= course.getId() %>">

        <div class="form-group">
            <label for="visibility">课程可见性 <span class="required">*</span></label>
            <select id="visibility" name="visibility" required>
                <option value="all" <%= "all".equals(course.getVisibility()) ? "selected" : "" %>>
                    全部学生可见
                </option>
                <option value="class_only" <%= "class_only".equals(course.getVisibility()) ? "selected" : "" %>>
                    仅本班学生可见
                </option>
            </select>
            <div class="help-text">
                <strong>全部学生可见：</strong>所有注册学生都可以看到并加入此课程<br>
                <strong>仅本班学生可见：</strong>只有指定班级的学生才能看到并加入此课程
            </div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">保存权限设置</button>
            <a href="${pageContext.request.contextPath}/teacher/courses" class="btn btn-secondary">取消</a>
        </div>
    </form>
    <% } else { %>
    <div class="message error">未找到课程信息</div>
    <a href="${pageContext.request.contextPath}/teacher/courses" class="btn btn-secondary">返回课程列表</a>
    <% } %>
</div>
</body>
</html>
