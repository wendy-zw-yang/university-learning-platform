<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.ResourceModel" %>
<%@ page import="com.ulp.bean.CourseModel" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    // 验证用户是否登录且为管理员
    Object userObj = session.getAttribute("user");
    if (userObj == null || !"admin".equals(userObj.getClass().getMethod("getRole").invoke(userObj))) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    // 获取课程列表和资源列表
    List<CourseModel> courses = (List<CourseModel>) request.getAttribute("courses");
    List<ResourceModel> allResources = (List<ResourceModel>) request.getAttribute("resources");

    // 获取当前选中的课程ID
    String courseIdParam = request.getParameter("courseId");
    Integer selectedCourseId = null;
    if (courseIdParam != null && !courseIdParam.isEmpty()) {
        try {
            selectedCourseId = Integer.parseInt(courseIdParam);
        } catch (NumberFormatException e) {
            // 如果参数无效，忽略
        }
    }

    // 获取当前操作的资源ID
    String resourceIdParam = request.getParameter("resourceId");
    Integer selectedResourceId = null;
    if (resourceIdParam != null && !resourceIdParam.isEmpty()) {
        try {
            selectedResourceId = Integer.parseInt(resourceIdParam);
        } catch (NumberFormatException e) {
            // 如果参数无效，忽略
        }
    }

    // 获取错误信息
    String errorMessage = (String) request.getAttribute("error");
    String successMessage = (String) request.getAttribute("success");

    // 格式化日期的工具
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>学习资源管理 - 管理员</title>
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
            max-width: 1400px;
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

        .actions-left {
            display: flex;
            gap: 10px;
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

        .btn-danger {
            background-color: #dc3545;
            color: white;
        }

        .btn-danger:hover {
            background-color: #c82333;
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

        .course-count {
            display: inline-block;
            background-color: #17a2b8;
            color: white;
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 12px;
        }

        .text-truncate {
            max-width: 200px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .course-list {
            margin-bottom: 30px;
        }

        .course-item {
            background: #f8f9fa;
            padding: 15px;
            margin-bottom: 10px;
            border-radius: 4px;
            border-left: 4px solid #007bff;
        }

        .course-item:hover {
            background: #e9ecef;
        }

        .course-item.active {
            background: #d1ecf1;
            border-left: 4px solid #17a2b8;
        }

        .course-link {
            text-decoration: none;
            color: #333;
            display: block;
        }

        .course-link:hover {
            color: #007bff;
        }

        .course-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .resource-detail {
            padding: 20px;
            background: #f8f9fa;
            border-radius: 8px;
            margin-top: 20px;
        }

        .resource-detail h3 {
            margin-top: 0;
            margin-bottom: 15px;
            color: #333;
        }

        .resource-info {
            margin-bottom: 15px;
        }

        .resource-info label {
            display: inline-block;
            width: 100px;
            font-weight: bold;
            color: #555;
        }

        .resource-info span {
            color: #666;
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
            max-height: 400px;
            overflow-y: auto;
        }

        .back-btn {
            margin-bottom: 15px;
        }

        .file-extension {
            display: inline-block;
            padding: 2px 8px;
            background-color: #6c757d;
            color: white;
            border-radius: 4px;
            font-size: 12px;
            margin-left: 10px;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>📝 学习资源管理</h1>

    <%-- 显示消息 --%>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="message error"><%= errorMessage %></div>
    <% } %>

    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="message success"><%= successMessage %></div>
    <% } %>

    <div class="actions">
        <div class="actions-left">
            <!-- 这里可以放置其他左侧操作按钮 -->
        </div>
        <a href="${pageContext.request.contextPath}/admin-homepage.jsp" class="btn btn-secondary">返回首页</a>
    </div>

    <div class="course-list">
        <h2>课程列表</h2>
        <% if (courses != null && !courses.isEmpty()) { %>
        <% for (CourseModel course : courses) {
            // 计算该课程的资源数量
            int resourceCount = 0;
            if (allResources != null) {
                for (ResourceModel resource : allResources) {
                    if (resource.getCourseId().equals(course.getId())) {
                        resourceCount++;
                    }
                }
            }
        %>
        <div class="course-item <%= selectedCourseId != null && selectedCourseId.equals(course.getId()) ? "active" : "" %>">
            <a href="${pageContext.request.contextPath}/admin/resource?courseId=<%= course.getId() %>" class="course-link">
                <div class="course-header">
                    <div>
                        <strong><%= course.getName() %></strong>
                        <span>ID: <%= course.getId() %></span>
                    </div>
                    <div>
                        <span class="course-count"><%= resourceCount %> 个资源</span>
                    </div>
                </div>
            </a>
        </div>
        <% } %>
        <% } else { %>
        <div class="no-data">
            <p>暂无课程数据</p>
        </div>
        <% } %>
    </div>

    <% if (selectedCourseId != null) { %>
    <h2>课程资源列表</h2>

    <%-- 筛选当前课程的资源 --%>
    <%
        List<ResourceModel> courseResources = null;
        if (allResources != null) {
            courseResources = new java.util.ArrayList<>();
            for (ResourceModel resource : allResources) {
                if (resource.getCourseId().equals(selectedCourseId)) {
                    courseResources.add(resource);
                }
            }
        }
    %>

    <% if (courseResources != null && !courseResources.isEmpty()) { %>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>资源名称</th>
            <th>上传者</th>
            <th>下载次数</th>
            <th>创建时间</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <% for (ResourceModel resource : courseResources) { %>
        <tr>
            <td><%= resource.getId() %></td>
            <td>
                <a href="${pageContext.request.contextPath}/admin/resource?courseId=<%= selectedCourseId %>&resourceId=<%= resource.getId() %>"
                   style="color: #007bff; text-decoration: none;">
                    <%= resource.getTitle() %>
                    <span class="file-extension">
                                            <%= resource.getFilePath().substring(resource.getFilePath().lastIndexOf('.') + 1).toUpperCase() %>
                                        </span>
                </a>
            </td>
            <td><%= resource.getUploaderId() %></td>
            <td><%= resource.getDownloadCount() %></td>
            <td><%= resource.getCreatedAt() != null ? dateFormat.format(resource.getCreatedAt()) : "-" %></td>
            <td>
                <div class="actions-cell">
                    <a href="${pageContext.request.contextPath}/admin/resource?action=edit&id=<%= resource.getId() %>&courseId=<%= selectedCourseId %>"
                       class="btn btn-warning">编辑</a>
                    <button onclick="confirmDelete(<%= resource.getId() %>, '<%= resource.getTitle() %>')"
                            class="btn btn-danger">删除</button>
                </div>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <% } else { %>
    <div class="no-data">
        <p>该课程暂无资源</p>
    </div>
    <% } %>
    <% } %>

    <% if (selectedResourceId != null) { %>
    <%
        ResourceModel selectedResource = null;
        if (allResources != null) {
            for (ResourceModel resource : allResources) {
                if (resource.getId().equals(selectedResourceId)) {
                    selectedResource = resource;
                    break;
                }
            }
        }
    %>

    <% if (selectedResource != null) { %>
    <div class="resource-detail">
        <div class="back-btn">
            <a href="${pageContext.request.contextPath}/admin/resource?courseId=<%= selectedCourseId %>"
               class="btn btn-secondary">← 返回课程资源列表</a>
        </div>

        <h3>资源详情: <%= selectedResource.getTitle() %></h3>

        <div class="resource-info">
            <p><label>资源ID:</label> <span><%= selectedResource.getId() %></span></p>
            <p><label>资源标题:</label> <span><%= selectedResource.getTitle() %></span></p>
            <p><label>资源描述:</label> <span><%= selectedResource.getDescription() != null ? selectedResource.getDescription() : "无描述" %></span></p>
            <p><label>上传者ID:</label> <span><%= selectedResource.getUploaderId() %></span></p>
            <p><label>课程ID:</label> <span><%= selectedResource.getCourseId() %></span></p>
            <p><label>下载次数:</label> <span><%= selectedResource.getDownloadCount() %></span></p>
            <p><label>创建时间:</label> <span><%= selectedResource.getCreatedAt() != null ? dateFormat.format(selectedResource.getCreatedAt()) : "-" %></span></p>
        </div>

        <div class="preview-container">
            <div class="preview-title">资源预览</div>
            <div class="preview-content">
                <%
                    String filePath = selectedResource.getFilePath();
                    String fileExtension = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();

                    if (fileExtension.equals("pdf")) {
                %>
                <iframe src="${pageContext.request.contextPath}/uploads/<%= filePath %>"
                        width="100%"
                        height="500px"
                        style="border: none;">
                    您的浏览器不支持预览PDF文件，请<a href="${pageContext.request.contextPath}/uploads/<%= filePath %>" target="_blank">点击这里下载</a>。
                </iframe>
                <%
                } else if (fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png") || fileExtension.equals("gif")) {
                %>
                <img src="${pageContext.request.contextPath}/uploads/<%= filePath %>"
                     alt="资源预览"
                     style="max-width: 100%; height: auto; border: 1px solid #ddd; padding: 10px;">
                <%
                } else {
                %>
                <p>该文件类型不支持在线预览。文件扩展名: <%= fileExtension.toUpperCase() %></p>
                <a href="${pageContext.request.contextPath}/uploads/<%= filePath %>"
                   target="_blank"
                   class="btn btn-primary">下载文件</a>
                <%
                    }
                %>
            </div>
        </div>

        <div class="actions" style="margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/admin/resource?action=edit&id=<%= selectedResource.getId() %>&courseId=<%= selectedCourseId %>"
               class="btn btn-warning">编辑资源信息</a>
            <button onclick="confirmDelete(<%= selectedResource.getId() %>, '<%= selectedResource.getTitle() %>')"
                    class="btn btn-danger">删除资源</button>
            <a href="${pageContext.request.contextPath}/admin/resource?courseId=<%= selectedCourseId %>"
               class="btn btn-secondary">返回课程资源列表</a>
        </div>
    </div>
    <% } %>
    <% } %>
</div>

<script>
    function confirmDelete(id, title) {
        if (confirm('确定要删除资源 "' + title + '" 吗？此操作不可恢复！')) {
            // 使用POST方式删除
            var form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/admin/resource';

            var actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'delete';
            form.appendChild(actionInput);

            var idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = id;
            form.appendChild(idInput);

            document.body.appendChild(form);
            form.submit();
        }
    }
</script>
</body>
</html>
