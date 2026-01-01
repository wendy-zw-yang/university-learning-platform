<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.UserModel" %>
<%@ page import="com.ulp.bean.ResourceModel" %>
<%@ page import="com.ulp.bean.QuestionModel" %>
<%@ page import="com.ulp.bean.AnswerModel" %>
<%@ page import="com.ulp.service.ResourceService" %>
<%@ page import="com.ulp.service.QuestionService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    // 验证用户是否登录且为学生
    UserModel user = (UserModel) session.getAttribute("user");
    if (user == null || !"student".equals(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    // 获取学生上传的资源
    ResourceService resourceService = new ResourceService();
    List<ResourceModel> userResources = resourceService.getResourcesByUploaderId(user.getId());

    // 获取学生提出的问题
    QuestionService questionService = new QuestionService();
    List<com.ulp.bean.QuestionWithAnswers> userQuestions = questionService.getQuestionsByStudentId(user.getId());

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
    <title>学生个人中心</title>
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

        /* 编辑和删除按钮颜色样式 */
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

        .question-content {
            margin: 10px 0;
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 4px;
            position: relative;
        }

        .answer-content {
            margin: 10px 0 10px 20px;
            padding: 10px;
            background-color: #e9f4ff;
            border-radius: 4px;
            border-left: 3px solid #007bff;
            position: relative;
        }

        .question-title {
            font-weight: bold;
            color: #333;
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

        .delete-btn {
            position: absolute;
            top: 10px;
            right: 10px;
            background-color: #dc3545;
            color: white;
            border: none;
            border-radius: 4px;
            padding: 5px 10px;
            cursor: pointer;
            font-size: 12px;
        }

        .delete-btn:hover {
            background-color: #c82333;
        }

        .update-btn {
            position: absolute;
            top: 10px;
            right: 100px;
            background-color: #ffc107;
            color: #212529;
            border: none;
            border-radius: 4px;
            padding: 5px 10px;
            cursor: pointer;
            font-size: 12px;
        }

        .update-btn:hover {
            background-color: #e0a800;
        }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <div class="header">
        <h1>🎓 学生个人中心</h1>
        <a href="${pageContext.request.contextPath}/student_homepage.jsp" class="btn btn-secondary">返回首页</a>
    </div>

    <%-- 显示消息 --%>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="message error"><%= errorMessage %></div>
    <% } %>

    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="message success"><%= successMessage %></div>
    <% } %>

    <h2>我上传的资源</h2>

    <% if (userResources != null && !userResources.isEmpty()) { %>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>资源名称</th>
            <th>课程ID</th>
            <th>下载次数</th>
            <th>创建时间</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <% for (ResourceModel resource : userResources) { %>
        <tr>
            <td><%= resource.getId() %></td>
            <td>
                <a href="${pageContext.request.contextPath}/student/center/resource/preview?id=<%= resource.getId() %>"
                   style="color: #007bff; text-decoration: none;">
                    <%= resource.getTitle() %>
                    <span class="file-extension">
                        <%= resource.getFilePath().substring(resource.getFilePath().lastIndexOf('.') + 1).toUpperCase() %>
                    </span>
                </a>
            </td>
            <td><%= resource.getCourseId() %></td>
            <td><%= resource.getDownloadCount() %></td>
            <td><%= resource.getCreatedAt() != null ? dateFormat.format(resource.getCreatedAt()) : "-" %></td>
            <td>
                <div class="actions-cell">
                    <a href="${pageContext.request.contextPath}/student/center/resource?action=edit&id=<%= resource.getId() %>"
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
        <p>您暂无上传的资源</p>
    </div>
    <% } %>

    <h2 style="margin-top: 40px;">我提出的问题</h2>

    <% if (userQuestions != null && !userQuestions.isEmpty()) { %>
    <% for (com.ulp.bean.QuestionWithAnswers questionWithAnswers : userQuestions) {
        QuestionModel question = questionWithAnswers.getQuestion();
        List<AnswerModel> answers = questionWithAnswers.getAnswers();
    %>
    <div class="question-content">
        <form method="post" action="${pageContext.request.contextPath}/student/center/question" style="display: inline;" onsubmit="return confirm('确定要删除这个问题及其所有回答吗？')">
            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="questionId" value="<%= question.getId() %>">
            <button type="submit" class="delete-btn">删除问题</button>
        </form>

        <!-- 修改按钮 -->
        <a href="${pageContext.request.contextPath}/student/center/question?action=edit&questionId=<%= question.getId() %>" class="update-btn">修改问题</a>

        <div class="question-title">
            <%= question.getTitle() %>
            <span style="font-weight: normal; color: #666; font-size: 14px; margin-left: 10px;">
                    课程ID: <%= question.getCourseId() %> |
                    时间: <%= question.getCreatedAt() != null ? dateFormat.format(question.getCreatedAt()) : "-" %>
                </span>
        </div>
        <div style="margin-top: 8px;" id="question-content-<%= question.getId() %>">
            <%= question.getContent() %>
        </div>
        <% if (question.getAttachment() != null && !question.getAttachment().isEmpty()) { %>
        <div>
            <a href="<%= request.getContextPath() + question.getAttachment() %>"
               class="attachment-link" target="_blank">附件: <%= question.getAttachment().substring(question.getAttachment().lastIndexOf('/') + 1) %></a>
        </div>
        <% } %>

        <!-- 显示回答 -->
        <div style="margin-top: 15px;">
            <strong>教师回答:</strong>
            <% if (answers != null && !answers.isEmpty()) { %>
            <% for (AnswerModel answer : answers) { %>
            <div class="answer-content" style="display: flex; align-items: center;">
                <div style="flex: 1;">
                    <div id="answer-content-<%= answer.getId() %>">
                        <%= answer.getContent() %>
                    </div>
                    <div style="font-size: 12px; color: #666; margin-top: 5px;">
                        教师:
                        <% if (answer.getTeacherName() != null) { %>
                        <%= answer.getTeacherName() %>
                        <% } else { %>
                        ID <%= answer.getTeacherId() %>
                        <% } %> |
                        时间: <%= answer.getCreatedAt() != null ? dateFormat.format(answer.getCreatedAt()) : "-" %>
                    </div>
                    <% if (answer.getAttachment() != null && !answer.getAttachment().isEmpty()) { %>
                    <div>
                        <a href="<%= request.getContextPath() + answer.getAttachment() %>"
                           class="attachment-link" target="_blank">附件: <%= answer.getAttachment().substring(answer.getAttachment().lastIndexOf('/') + 1) %></a>
                    </div>
                    <% } %>
                </div>
            </div>
            <% } %>
            <% } else { %>
            <div style="color: #dc3545; font-style: italic; margin-top: 10px;">暂无回答</div>
            <% } %>
        </div>
    </div>
    <% } %>
    <% } else { %>
    <div class="no-data">
        <p>您暂无提出的问题</p>
    </div>
    <% } %>
</div>

<script>
    function confirmDelete(id, title) {
        if (confirm('确定要删除资源 "' + title + '" 吗？此操作不可恢复！')) {
            // 使用POST方式删除
            var form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/student/center/resource';

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
