<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.CourseModel" %>
<%@ page import="java.util.List" %>
<%
    // 验证用户是否登录且为学生
    Object userObj = session.getAttribute("user");
    if (userObj == null || !"student".equals(userObj.getClass().getMethod("getRole").invoke(userObj))) {
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
            // 如果参数无效，重定向回资源列表
            response.sendRedirect(request.getContextPath() + "/student/resource");
            return;
        }
    }

    // 如果没有获取到课程信息，尝试通过ID获取
    if (course == null && courseId != null) {
        course = new com.ulp.service.CourseService().getCourseById(courseId);
    }

    // 获取用户信息
    com.ulp.bean.UserModel user = (com.ulp.bean.UserModel) session.getAttribute("user");

    // 获取消息信息
    String errorMessage = (String) request.getAttribute("error");
    String successMessage = (String) request.getAttribute("success");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>上传学习资源 - 大学生学习平台</title>
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

        /* 模态框样式 */
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }

        .modal-content {
            background-color: #fefefe;
            margin: 15% auto;
            padding: 20px;
            border: none;
            border-radius: 8px;
            width: 300px;
            text-align: center;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
            position: relative;
        }

        .modal-header {
            padding: 15px;
            text-align: center;
            border-radius: 8px 8px 0 0;
            margin: -20px -20px 15px -20px;
        }

        .modal-success {
            background-color: #d4edda;
            color: #155724;
        }

        .modal-error {
            background-color: #f8d7da;
            color: #721c24;
        }

        .modal-body {
            padding: 15px;
        }

        .close {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
            position: absolute;
            right: 10px;
            top: 5px;
            cursor: pointer;
        }

        .close:hover {
            color: #000;
        }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <h1>📚 上传学习资源 - <%= course != null ? course.getName() : "课程" %></h1>

    <form id="uploadForm" method="post" action="${pageContext.request.contextPath}/student/resource" enctype="multipart/form-data">
        <input type="hidden" name="action" value="upload">
        <input type="hidden" name="uploaderId" value="<%= user.getId() %>">
        <input type="hidden" name="courseId" value="<%= courseId %>">

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
            <a href="${pageContext.request.contextPath}/student/resource" class="btn btn-secondary">返回课程列表</a>
        </div>
    </form>
</div>

<!-- 模态框 -->
<div id="messageModal" class="modal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <div class="modal-header" id="modalHeader">
            <h2 id="modalTitle">上传结果</h2>
        </div>
        <div class="modal-body">
            <p id="modalMessage"></p>
        </div>
    </div>
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

    // 获取模态框元素
    const modal = document.getElementById('messageModal');
    const modalHeader = document.getElementById('modalHeader');
    const modalTitle = document.getElementById('modalTitle');
    const modalMessage = document.getElementById('modalMessage');
    const closeBtn = document.getElementsByClassName('close')[0];

    // 显示模态框的函数
    function showModal(message, isSuccess) {
        if (isSuccess) {
            modalHeader.className = 'modal-header modal-success';
            modalTitle.textContent = '上传成功';
        } else {
            modalHeader.className = 'modal-header modal-error';
            modalTitle.textContent = '上传失败';
        }
        modalMessage.textContent = message;
        modal.style.display = 'block';
    }

    // 关闭模态框
    closeBtn.onclick = function() {
        modal.style.display = 'none';
        if (!<%= successMessage != null && !successMessage.isEmpty() %>) {
            // 如果是错误消息，不刷新页面
        } else {
            // 如果是成功消息，刷新页面或跳转
            window.location.href = '${pageContext.request.contextPath}/student/resource?courseId=' + <%= courseId %>;
        }
    }

    // 点击模态框外部关闭
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
            if (!<%= successMessage != null && !successMessage.isEmpty() %>) {
                // 如果是错误消息，不刷新页面
            } else {
                // 如果是成功消息，刷新页面或跳转
                window.location.href = '${pageContext.request.contextPath}/student/resource?courseId=' + <%= courseId %>;
            }
        }
    }

    // 如果有消息，显示模态框
    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    document.addEventListener('DOMContentLoaded', function() {
        showModal('<%= successMessage %>', true);
    });
    <% } else if (errorMessage != null && !errorMessage.isEmpty()) { %>
    document.addEventListener('DOMContentLoaded', function() {
        showModal('<%= errorMessage %>', false);
    });
    <% } %>
</script>
</body>
</html>
