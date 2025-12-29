<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.CourseModel" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>提问 - 大学生学习平台</title>
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
        input[type="file"],
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
        input[type="file"]:focus,
        textarea:focus,
        select:focus {
            outline: none;
            border-color: #007bff;
        }
        
        textarea {
            resize: vertical;
            min-height: 150px;
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
    </style>
</head>
<body>
    <div class="container">
        <%
            CourseModel course = (CourseModel) request.getAttribute("course");
        %>
        
        <h1>向课程提问</h1>
        <p>课程名称: <strong><%= course != null ? course.getName() : "未知课程" %></strong></p>
        
        <%-- 显示错误或成功消息 --%>
        <% if (request.getAttribute("error") != null) { %>
            <div class="message error"><%= request.getAttribute("error") %></div>
        <% } %>
        <% if (request.getAttribute("success") != null) { %>
            <div class="message success"><%= request.getAttribute("success") %></div>
        <% } %>
        
        <form method="post" action="${pageContext.request.contextPath}/questions" enctype="multipart/form-data">
            <input type="hidden" name="action" value="ask">
            <% if (course != null) { %>
                <input type="hidden" name="courseId" value="<%= course.getId() %>">
            <% } %>
            
            <div class="form-group">
                <label for="title">提问标题 <span class="required">*</span></label>
                <input type="text" 
                       id="title" 
                       name="title" 
                       required 
                       placeholder="请简明扼要地描述您的问题标题">
                <div class="help-text">标题应简明扼要，准确概括问题核心</div>
            </div>
            
            <div class="form-group">
                <label for="content">问题内容 <span class="required">*</span></label>
                <textarea id="content" 
                          name="content" 
                          placeholder="请详细描述您的问题，包括您已经尝试过的解决方案..."></textarea>
                <div class="help-text">请尽可能详细地描述您的问题，包括背景信息和已尝试的方法</div>
            </div>
            
            <div class="form-group">
                <label for="attachment">附件上传</label>
                <input type="file" id="attachment" name="attachment" accept="image/*">
                <div class="help-text">可上传图片作为问题附件，如截图、公式推导图等（支持JPG、PNG等图片格式）</div>
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">提交问题</button>
                <a href="${pageContext.request.contextPath}/student/questions" class="btn btn-secondary">返回</a>
            </div>
        </form>
    </div>
</body>
</html>