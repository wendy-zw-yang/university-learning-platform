<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.UserModel" %>
<%@ page import="com.ulp.bean.CourseModel" %>
<%@ page import="java.util.List" %>
<%
    // 验证用户是否登录且为教师
    UserModel user = (UserModel) session.getAttribute("user");
    if (user == null || !"teacher".equals(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    // 获取课程信息
    CourseModel course = (CourseModel) request.getAttribute("course");
    String courseIdParam = request.getParameter("courseId");
    Integer courseId = null;
    if (courseIdParam != null && !courseIdParam.isEmpty()) {
        try {
            courseId = Integer.parseInt(courseIdParam);
        } catch (NumberFormatException e) {
            // 如果参数无效，重定向
            response.sendRedirect(request.getContextPath() + "/teacher/resource");
            return;
        }
    }

    // 获取错误信息
    String errorMessage = (String) request.getAttribute("error");
    String successMessage = (String) request.getAttribute("success");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>上传资源 - <%= course != null ? course.getName() : "课程" %> - 项目管理学习平台</title>
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

        .course-header {
            background: #e3f2fd;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 30px;
            border-left: 4px solid #2196f3;
        }

        .course-name {
            font-size: 20px;
            font-weight: bold;
            color: #1976d2;
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

        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
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

        .file-input-wrapper {
            position: relative;
            display: inline-block;
            cursor: pointer;
            background-color: #f8f9fa;
            border: 2px dashed #ddd;
            border-radius: 4px;
            padding: 20px;
            text-align: center;
            width: 100%;
        }

        .file-input-wrapper input[type="file"] {
            position: absolute;
            opacity: 0;
            width: 100%;
            height: 100%;
            cursor: pointer;
        }

        .file-input-text {
            display: block;
            color: #6c757d;
        }

        .file-name {
            margin-top: 10px;
            font-size: 14px;
            color: #495057;
        }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <h1>📚 上传学习资源</h1>

    <div class="course-header">
        <div class="course-name">课程: <%= course != null ? course.getName() : "未知课程" %></div>
        <div>课程ID: <%= courseId != null ? courseId : "未知" %></div>
    </div>

    <%-- 显示消息 --%>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="message error"><%= errorMessage %></div>
    <% } %>

    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="message success"><%= successMessage %></div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/teacher/resource" enctype="multipart/form-data">
        <input type="hidden" name="action" value="upload">
        <input type="hidden" name="courseId" value="<%= courseId != null ? courseId : "" %>">
        <input type="hidden" name="uploaderId" value="<%= user.getId() %>">

        <div class="form-group">
            <label for="title">资源标题 <span class="required">*</span></label>
            <input type="text"
                   id="title"
                   name="title"
                   required
                   maxlength="100"
                   placeholder="请输入资源标题">
            <div class="help-text">资源的名称，最多100个字符</div>
        </div>

        <div class="form-group">
            <label for="description">资源描述</label>
            <textarea id="description"
                      name="description"
                      placeholder="请输入资源的详细描述"></textarea>
            <div class="help-text">对资源内容的详细说明</div>
        </div>

        <div class="form-group">
            <label for="file">上传附件 <span class="required">*</span></label>
            <div class="file-input-wrapper">
                <input type="file" id="file" name="file" accept=".pdf,.doc,.docx,.ppt,.pptx,.jpg,.jpeg,.png,.gif,.txt,.zip" required>
                <span class="file-input-text">点击或拖拽文件到此处上传</span>
                <span class="help-text">支持PDF、Word、PPT、图片、文本、ZIP等格式，最大100MB</span>
            </div>
            <div id="file-name" class="file-name" style="display:none;"></div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">上传资源</button>
            <a href="${pageContext.request.contextPath}/teacher/resource" class="btn btn-secondary">返回课程列表</a>
        </div>
    </form>
</div>

<script>
    // 显示选择的文件名
    document.getElementById('file').addEventListener('change', function(e) {
        const fileName = e.target.files[0] ? e.target.files[0].name : '';
        const fileNameDiv = document.getElementById('file-name');

        if (fileName) {
            fileNameDiv.textContent = '已选择: ' + fileName;
            fileNameDiv.style.display = 'block';
        } else {
            fileNameDiv.style.display = 'none';
        }
    });

    // 拖拽上传功能
    const fileInputWrapper = document.querySelector('.file-input-wrapper');
    const fileInput = document.getElementById('file');

    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        fileInputWrapper.addEventListener(eventName, preventDefaults, false);
    });

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    ['dragenter', 'dragover'].forEach(eventName => {
        fileInputWrapper.addEventListener(eventName, highlight, false);
    });

    ['dragleave', 'drop'].forEach(eventName => {
        fileInputWrapper.addEventListener(eventName, unhighlight, false);
    });

    function highlight(e) {
        fileInputWrapper.style.borderColor = '#007bff';
        fileInputWrapper.style.backgroundColor = '#e6f7ff';
    }

    function unhighlight(e) {
        fileInputWrapper.style.borderColor = '#ddd';
        fileInputWrapper.style.backgroundColor = '#f8f9fa';
    }

    fileInputWrapper.addEventListener('drop', handleDrop, false);

    function handleDrop(e) {
        const dt = e.dataTransfer;
        const files = dt.files;
        fileInput.files = files;

        const fileName = files[0] ? files[0].name : '';
        const fileNameDiv = document.getElementById('file-name');

        if (fileName) {
            fileNameDiv.textContent = '已选择: ' + fileName;
            fileNameDiv.style.display = 'block';
        }
    }
</script>
</body>
</html>
