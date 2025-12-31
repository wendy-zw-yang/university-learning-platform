<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.ResourceModel" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Timestamp" %>
<%
    // 验证用户是否登录且为学生
    Object userObj = session.getAttribute("user");
    if (userObj == null || !"student".equals(userObj.getClass().getMethod("getRole").invoke(userObj))) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    // 获取资源信息
    ResourceModel resource = (ResourceModel) request.getAttribute("resource");
    String resourceIdParam = request.getParameter("id");
    Integer resourceId = null;
    if (resourceIdParam != null && !resourceIdParam.isEmpty()) {
        try {
            resourceId = Integer.parseInt(resourceIdParam);
        } catch (NumberFormatException e) {
            // 如果参数无效，重定向回资源列表
            response.sendRedirect(request.getContextPath() + "/student/resource");
            return;
        }
    }

    if (resource == null) {
        // 如果没有从request获取到资源信息，重定向回资源列表
        response.sendRedirect(request.getContextPath() + "/student/resource");
        return;
    }

    // 格式化日期的工具
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>资源预览 - <%= resource.getTitle() %> - 学生</title>
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
            padding: 30px;
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

        .resource-info {
            margin-bottom: 30px;
            padding: 20px;
            background: #f8f9fa;
            border-radius: 8px;
        }

        .resource-info h2 {
            margin-top: 0;
            margin-bottom: 15px;
            color: #333;
        }

        .info-row {
            display: flex;
            margin-bottom: 10px;
        }

        .info-label {
            font-weight: bold;
            color: #555;
            width: 120px;
            flex-shrink: 0;
        }

        .info-value {
            color: #666;
            flex-grow: 1;
        }

        .preview-container {
            margin-top: 20px;
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 15px;
            background: white;
        }

        .preview-title {
            font-weight: bold;
            margin-bottom: 10px;
            color: #333;
        }

        .preview-content {
            max-height: 600px;
            overflow-y: auto;
        }

        .unsupported-file {
            margin-top: 15px;
        }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <div class="header">
        <h1>📋 资源预览</h1>
        <a href="javascript:history.back()" class="btn btn-secondary">返回列表</a>
    </div>

    <div class="resource-info">
        <h2><%= resource.getTitle() %></h2>
        <div class="info-row">
            <div class="info-label">资源ID:</div>
            <div class="info-value"><%= resource.getId() %></div>
        </div>
        <div class="info-row">
            <div class="info-label">资源描述:</div>
            <div class="info-value"><%= resource.getDescription() != null ? resource.getDescription() : "无描述" %></div>
        </div>
        <div class="info-row">
            <div class="info-label">上传者ID:</div>
            <div class="info-value"><%= resource.getUploaderId() %></div>
        </div>
        <div class="info-row">
            <div class="info-label">课程ID:</div>
            <div class="info-value"><%= resource.getCourseId() %></div>
        </div>
        <div class="info-row">
            <div class="info-label">下载次数:</div>
            <div class="info-value"><%= resource.getDownloadCount() %></div>
        </div>
        <div class="info-row">
            <div class="info-label">创建时间:</div>
            <div class="info-value"><%= resource.getCreatedAt() != null ? dateFormat.format(resource.getCreatedAt()) : "-" %></div>
        </div>
    </div>

    <div class="preview-container">
        <div class="preview-title">资源内容预览</div>
        <div class="preview-content">
            <p>文件类型: <%= resource.getFilePath().substring(resource.getFilePath().lastIndexOf('.') + 1).toUpperCase() %></p>
            <p>文件名: <%= resource.getFilePath() %></p>
            <div class="unsupported-file">
                <a href="${pageContext.request.contextPath}/uploads/<%= resource.getFilePath() %>"
                   target="_blank"
                   class="btn btn-primary">下载文件</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>