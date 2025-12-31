<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.ResourceModel" %>
<%@ page import="com.ulp.bean.CourseModel" %>
<%@ page import="com.ulp.service.UserService" %>
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
            max-width: 1400px;
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

        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        .btn-secondary:hover {
            background-color: #5a6268;
        }

        /* 新增：编辑和删除按钮颜色样式 */
        .btn-edit {
            background-color: #ffc107;
            color: #212529;
        }
        .btn-edit:hover {
            background-color: #e0a800;
        }
        .btn-delete {
            background-color: #dc3545;
            color: white;
        }
        .btn-delete:hover {
            background-color: #c82333;
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

        .course-list {
            margin-bottom: 30px;
        }

        .course-list h2 {
            margin-bottom: 20px;
        }

        .course-item {
            background: #f8f9fa;
            padding: 15px;
            margin-bottom: 10px;
            border-radius: 4px;
            border-left: 4px solid #007bff;
            display: flex;
            flex-direction: column;
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
            display: flex;
            flex-direction: column;
        }

        .course-link:hover {
            color: #007bff;
        }

        .course-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 5px;
        }

        .course-count {
            display: inline-block;
            background-color: #17a2b8;
            color: white;
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 12px;
            align-self: center;
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-50%);
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
<%@ include file="navbar.jsp" %>
<div class="container">
    <div class="header">
        <h1>📝 学习资源管理</h1>
        <a href="${pageContext.request.contextPath}/admin_homepage.jsp" class="btn btn-secondary">返回首页</a>
    </div>

    <%-- 显示消息 --%>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="message error"><%= errorMessage %></div>
    <% } %>

    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="message success"><%= successMessage %></div>
    <% } %>

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

            // 获取教师名称
            String teacherName = null;
            if (course.getTeacherId() != null) {
                UserService userService = new UserService();
                teacherName = userService.getUsernameById(course.getTeacherId());
            }
        %>
        <div class="course-item <%= selectedCourseId != null && selectedCourseId.equals(course.getId()) ? "active" : "" %>" style="position: relative;">
            <a href="${pageContext.request.contextPath}/admin/resource?courseId=<%= course.getId() %>" class="course-link">
                <div class="course-header">
                    <div>
                        <strong><%= course.getName() %></strong>
                        <span>ID: <%= course.getId() %></span>
                        <% if (teacherName != null) { %>
                        <span> | 教师: <%= teacherName %></span>
                        <% } %>
                    </div>
                </div>
                <div class="course-count"><%= resourceCount %> 个资源</div>
                <% if (course.getDescription() != null && !course.getDescription().isEmpty()) { %>
                <div><%= course.getDescription() %></div>
                <% } %>
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
                <a href="${pageContext.request.contextPath}/admin/resource/preview?id=<%= resource.getId() %>"
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
                       class="btn btn-edit">编辑</a>
                    <button onclick="confirmDelete(<%= resource.getId() %>, '<%= resource.getTitle() %>')"
                            class="btn btn-delete">删除</button>
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
