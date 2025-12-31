<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.ResourceModel" %>
<%@ page import="com.ulp.bean.CourseModel" %>
<%@ page import="com.ulp.service.UserService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    // 验证用户是否登录且为学生
    Object userObj = session.getAttribute("user");
    if (userObj == null || !"student".equals(userObj.getClass().getMethod("getRole").invoke(userObj))) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    // 获取课程列表和资源列表
    List<CourseModel> courses = (List<CourseModel>) request.getAttribute("courses");
    List<ResourceModel> allResources = (List<ResourceModel>) request.getAttribute("resources");
    Integer selectedCourseId = (Integer) request.getAttribute("selectedCourseId");

    // 获取搜索参数
    String searchQuery = request.getParameter("search");
    if (searchQuery == null) {
        searchQuery = "";
    }

    // 获取错误信息
    String errorMessage = (String) request.getAttribute("error");
    String successMessage = (String) request.getAttribute("success");

    // 创建UserService实例
    UserService userService = new UserService();

    // 格式化日期的工具
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>学习资源管理 - 学生</title>
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

        .search-container {
            margin-bottom: 30px;
            display: flex;
            gap: 10px;
        }

        .search-input {
            flex: 1;
            padding: 10px 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 16px;
        }

        .search-btn {
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        .search-btn:hover {
            background-color: #0056b3;
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
            display: flex;
            justify-content: space-between;
            align-items: center;
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
            flex: 1;
        }

        .course-link:hover {
            color: #007bff;
        }

        .course-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .course-count {
            display: inline-block;
            background-color: #17a2b8;
            color: white;
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 12px;
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
        <h1>📚 学习资源管理</h1>
        <a href="${pageContext.request.contextPath}/student_homepage.jsp" class="btn btn-secondary">返回首页</a>
    </div>

    <!-- 搜索框 -->
    <div class="search-container">
        <form method="get" action="${pageContext.request.contextPath}/student/resource" style="display: flex; width: 100%; gap: 10px;">
            <input type="text"
                   name="search"
                   class="search-input"
                   placeholder="搜索资源名称..."
                   value="<%= searchQuery != null ? searchQuery : "" %>">
            <button type="submit" class="search-btn">搜索</button>
            <% if (searchQuery != null && !searchQuery.isEmpty()) { %>
            <a href="${pageContext.request.contextPath}/student/resource" class="btn btn-secondary">清除搜索</a>
            <% } %>
        </form>
    </div>

    <%-- 显示消息 --%>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="message error"><%= errorMessage %></div>
    <% } %>

    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="message success"><%= successMessage %></div>
    <% } %>

    <!-- 搜索结果或课程列表 -->
    <% if (searchQuery != null && !searchQuery.isEmpty()) { %>
    <!-- 搜索结果 -->
    <h2>搜索结果</h2>

    <%
        // 根据搜索词筛选资源
        List<ResourceModel> searchResults = new java.util.ArrayList<>();
        if (allResources != null) {
            for (ResourceModel resource : allResources) {
                if (resource.getTitle().toLowerCase().contains(searchQuery.toLowerCase())) {
                    searchResults.add(resource);
                }
            }
        }
    %>

    <% if (searchResults != null && !searchResults.isEmpty()) { %>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>资源名称</th>
            <th>课程</th>
            <th>上传者</th>
            <th>下载次数</th>
            <th>创建时间</th>
        </tr>
        </thead>
        <tbody>
        <% for (ResourceModel resource : searchResults) {
            String uploaderName = userService.getUsernameById(resource.getUploaderId());
            CourseModel resourceCourse = null;
            if (courses != null) {
                for (CourseModel course : courses) {
                    if (course.getId() == resource.getCourseId().intValue()) {
                        resourceCourse = course;
                        break;
                    }
                }
            }
        %>
        <tr>
            <td><%= resource.getId() %></td>
            <td>
                <a href="${pageContext.request.contextPath}/student/resource/preview?id=<%= resource.getId() %>"
                   style="color: #007bff; text-decoration: none;">
                    <%= resource.getTitle() %>
                    <span class="file-extension">
                            <%= resource.getFilePath().substring(resource.getFilePath().lastIndexOf('.') + 1).toUpperCase() %>
                        </span>
                </a>
            </td>
            <td><%= resourceCourse != null ? resourceCourse.getName() : "未知课程" %></td>
            <td><%= uploaderName != null ? uploaderName : "未知用户" %></td>
            <td><%= resource.getDownloadCount() %></td>
            <td><%= resource.getCreatedAt() != null ? dateFormat.format(resource.getCreatedAt()) : "-" %></td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <% } else { %>
    <div class="no-data">
        <p>未找到匹配的资源</p>
    </div>
    <% } %>
    <% } else { %>
    <!-- 原有的课程列表和资源列表 -->
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
            <a href="${pageContext.request.contextPath}/student/resource?courseId=<%= course.getId() %>" class="course-link">
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
            <a href="${pageContext.request.contextPath}/student/edit_resources?courseId=<%= course.getId() %>" class="btn btn-primary">上传资源</a>
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
        </tr>
        </thead>
        <tbody>
        <% for (ResourceModel resource : courseResources) {
            String uploaderName = userService.getUsernameById(resource.getUploaderId());
        %>
        <tr>
            <td><%= resource.getId() %></td>
            <td>
                <a href="${pageContext.request.contextPath}/student/resource/preview?id=<%= resource.getId() %>"
                   style="color: #007bff; text-decoration: none;">
                    <%= resource.getTitle() %>
                    <span class="file-extension">
                            <%= resource.getFilePath().substring(resource.getFilePath().lastIndexOf('.') + 1).toUpperCase() %>
                        </span>
                </a>
            </td>
            <td><%= uploaderName != null ? uploaderName : "未知用户" %></td>
            <td><%= resource.getDownloadCount() %></td>
            <td><%= resource.getCreatedAt() != null ? dateFormat.format(resource.getCreatedAt()) : "-" %></td>
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
    <% } %>
</div>
</body>
</html>
