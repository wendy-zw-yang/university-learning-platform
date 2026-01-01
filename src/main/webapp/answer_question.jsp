<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.QuestionModel" %>
<%@ page import="com.ulp.service.QuestionService" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>回答问题 - 大学生学习平台</title>
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
        
        .question-preview {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 4px;
            margin-bottom: 20px;
            border-left: 4px solid #007bff;
        }
        
        .question-title {
            font-weight: bold;
            color: #333;
            margin-bottom: 10px;
            font-size: 18px;
        }
        
        .question-content {
            color: #555;
            line-height: 1.6;
        }
        
        .question-info {
            font-size: 14px;
            color: #666;
            margin-top: 10px;
        }
        
        .attachment-link {
            display: inline-block;
            margin-top: 5px;
            color: #007bff;
            text-decoration: none;
        }
        
        .attachment-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
    <div class="container">
        <%
            // 获取问题ID
            String questionIdParam = request.getParameter("questionId");
            if (questionIdParam == null) {
                questionIdParam = String.valueOf(request.getAttribute("questionId"));
            }
            
            QuestionModel question = null;
            if (questionIdParam != null && !questionIdParam.isEmpty()) {
                try {
                    int questionId = Integer.parseInt(questionIdParam);
                    question = new QuestionService().getQuestionById(questionId);
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "问题ID格式不正确");
                }
            }
        %>
        
        <h1>回答学生问题</h1>
        
        <% if (question != null) { %>
        <div class="question-preview">
            <div class="question-title"><%= question.getTitle() %></div>
            <div class="question-content"><%= question.getContent() %></div>
            <div class="question-info">
                学生ID: <%= question.getStudentId() %> | 
                课程ID: <%= question.getCourseId() %> | 
                提问时间: <%= question.getCreatedAt() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(question.getCreatedAt()) : "-" %>
            </div>
            <% if (question.getAttachment() != null && !question.getAttachment().isEmpty()) { %>
            <div>
                <a href="<%= request.getContextPath() + question.getAttachment() %>" 
                   class="attachment-link" target="_blank">查看附件: <%= question.getAttachment().substring(question.getAttachment().lastIndexOf('/') + 1) %></a>
            </div>
            <% } %>
        </div>
        <% } %>
        
        <%-- 显示错误或成功消息 --%>
        <% if (request.getAttribute("error") != null) { %>
            <div class="message error"><%= request.getAttribute("error") %></div>
        <% } %>
        <% if (request.getAttribute("success") != null) { %>
            <div class="message success"><%= request.getAttribute("success") %></div>
        <% } %>
        
        <form method="post" action="${pageContext.request.contextPath}/questions" enctype="multipart/form-data">
            <input type="hidden" name="action" value="answer">
            <input type="hidden" name="questionId" value="<%= question != null ? question.getId() : (request.getAttribute("questionId") != null ? request.getAttribute("questionId") : "") %>">
            
            <div class="form-group">
                <label for="content">回答内容 <span class="required">*</span></label>
                <textarea id="content" 
                          name="content" 
                          placeholder="请输入您的回答内容..."></textarea>
                <div class="help-text">请详细回答学生的问题，提供清晰的解释和帮助</div>
            </div>
            
            <div class="form-group">
                <label for="attachment">附件上传</label>
                <input type="file" id="attachment" name="attachment" accept="image/*">
                <div class="help-text">可上传图片作为回答附件，如截图、公式推导图等（支持JPG、PNG等图片格式）</div>
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">提交回答</button>
                <a href="${pageContext.request.contextPath}/teacher/questions" class="btn btn-secondary">返回</a>
            </div>
        </form>
    </div>
</body>
</html>