<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.AnswerModel" %>
<%@ page import="com.ulp.bean.UserModel" %>
<%
    // 获取当前登录用户
    UserModel userObj = (UserModel) session.getAttribute("user");
    if (userObj == null || !"teacher".equals(userObj.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // 获取要编辑的回答和课程信息
    AnswerModel answer = (AnswerModel) request.getAttribute("answer");
    Integer courseId = (Integer) request.getAttribute("courseId");
    
    String errorMessage = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>编辑回答 - 教师</title>
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

        .form-group {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #333;
        }

        textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            resize: vertical;
            font-family: inherit;
            font-size: 14px;
        }

        textarea:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 0 2px rgba(0,123,255,0.25);
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

        .answer-preview {
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>📝 编辑回答</h1>
        <a href="${pageContext.request.contextPath}/teacher/questions?courseId=${courseId}" class="btn btn-secondary">返回问题列表</a>
    </div>

    <%-- 显示错误信息 --%>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="message error"><%= errorMessage %></div>
    <% } %>

    <% if (answer != null) { %>
    <form method="post" action="${pageContext.request.contextPath}/teacher/questions">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="answerId" value="<%= answer.getId() %>">
        <input type="hidden" name="courseId" value="<%= courseId %>">
        
        <div class="answer-preview">
            <h3>原始回答内容:</h3>
            <p><%= answer.getContent() %></p>
        </div>
        
        <div class="form-group">
            <label for="newContent">修改回答内容:</label>
            <textarea id="newContent" name="newContent" rows="10" required><%= answer.getContent() %></textarea>
        </div>
        
        <div style="text-align: center;">
            <button type="submit" class="btn btn-primary">更新回答</button>
            <a href="${pageContext.request.contextPath}/teacher/questions?courseId=${courseId}" class="btn btn-secondary">取消</a>
        </div>
    </form>
    <% } else { %>
    <div class="message error">未找到要编辑的回答</div>
    <a href="${pageContext.request.contextPath}/teacher/questions?courseId=${courseId}" class="btn btn-secondary">返回问题列表</a>
    <% } %>
</div>
</body>
</html>