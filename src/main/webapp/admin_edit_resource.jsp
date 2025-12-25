<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.ResourceModel" %>
<%@ page import="java.util.List" %>
<%
    // 验证用户是否登录且为管理员
    Object userObj = session.getAttribute("user");
    if (userObj == null || !"admin".equals(userObj.getClass().getMethod("getRole").invoke(userObj))) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    // 获取资源信息
    ResourceModel resource = (ResourceModel) request.getAttribute("resource");
    String courseIdParam = request.getParameter("courseId");
    Integer courseId = null;
    if (courseIdParam != null && !courseIdParam.isEmpty()) {
        try {
            courseId = Integer.parseInt(courseIdParam);
        } catch (NumberFormatException e) {
            // 如果参数无效，忽略
        }
    }

    // 获取错误信息
    String errorMessage = (String) request.getAttribute("error");
    boolean isEdit = resource != null;
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= isEdit ? "编辑资源" : "添加资源" %> - 大学生学习平台</title>
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
        textarea {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            font-family: inherit;
            transition: border-color 0.3s;
        }

        input[type="text"]:focus,
        textarea:focus {
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
    <h1><%= isEdit ? "编辑资源信息" : "添加资源" %></h1>

    <%-- 显示错误消息 --%>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="message error"><%= errorMessage %></div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/admin/resource">
        <% if (isEdit) { %>
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="id" value="<%= resource.getId() %>">
        <input type="hidden" name="courseId" value="<%= courseId %>">
        <% } else { %>
        <input type="hidden" name="action" value="add">
        <% } %>

        <div class="form-group">
            <label for="title">资源标题 <span class="required">*</span></label>
            <input type="text"
                   id="title"
                   name="title"
                   value="<%= isEdit ? resource.getTitle() : "" %>"
                   required
                   maxlength="100"
                   placeholder="请输入资源标题">
            <div class="help-text">资源的名称，最多100个字符</div>
        </div>

        <div class="form-group">
            <label for="description">资源描述</label>
            <textarea id="description"
                      name="description"
                      placeholder="请输入资源的详细描述"><%= isEdit && resource.getDescription() != null ? resource.getDescription() : "" %></textarea>
            <div class="help-text">对资源内容的详细说明</div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">
                <%= isEdit ? "保存修改" : "添加资源" %>
            </button>
            <a href="${pageContext.request.contextPath}/admin/resource<%= courseId != null ? "?courseId=" + courseId : "" %>" class="btn btn-secondary">取消</a>
        </div>
    </form>
</div>
</body>
</html>
