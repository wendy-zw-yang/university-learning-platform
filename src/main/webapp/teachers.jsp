<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ulp.bean.TeacherModel" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>教师管理 - 大学生学习平台</title>
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
    </style>
    <script>
        function confirmDelete(id, name) {
            if (confirm('确定要删除教师"' + name + '"吗？此操作不可恢复！')) {
                // 使用POST方式删除
                var form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/admin/teachers';
                
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
</head>
<body>
    <div class="container">
        <h1>教师管理</h1>
        
        <%-- 显示成功消息 --%>
        <% if (request.getParameter("success") != null) { 
            String success = request.getParameter("success");
            String message = "";
            if ("add".equals(success)) message = "教师添加成功！";
            else if ("update".equals(success)) message = "教师信息更新成功！";
            else if ("delete".equals(success)) message = "教师删除成功！";
        %>
            <div class="message success"><%= message %></div>
        <% } %>
        
        <%-- 显示错误消息 --%>
        <% if (request.getAttribute("error") != null) { %>
            <div class="message error"><%= request.getAttribute("error") %></div>
        <% } %>
        
        <% if (request.getParameter("error") != null) { 
            String error = request.getParameter("error");
            String message = "";
            if ("delete".equals(error)) message = "删除教师失败！";
            else if ("invalid".equals(error)) message = "无效的教师ID！";
        %>
            <div class="message error"><%= message %></div>
        <% } %>
        
        <div class="actions">
            <a href="${pageContext.request.contextPath}/admin/teachers?action=add" class="btn btn-primary">+ 添加新教师</a>
            <a href="${pageContext.request.contextPath}/dashboard.jsp" class="btn btn-secondary">返回首页</a>
        </div>
        
        <%
            List<TeacherModel> teachers = (List<TeacherModel>) request.getAttribute("teachers");
            if (teachers != null && !teachers.isEmpty()) {
        %>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>用户名</th>
                        <th>邮箱</th>
                        <th>职称</th>
                        <th>简介</th>
                        <th>授课数量</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (TeacherModel teacher : teachers) { %>
                        <tr>
                            <td><%= teacher.getId() %></td>
                            <td><strong><%= teacher.getUsername() %></strong></td>
                            <td><%= teacher.getEmail() != null && !teacher.getEmail().isEmpty() ? teacher.getEmail() : "-" %></td>
                            <td><%= teacher.getTitle() != null && !teacher.getTitle().isEmpty() ? teacher.getTitle() : "-" %></td>
                            <td>
                                <div class="text-truncate" title="<%= teacher.getProfile() != null ? teacher.getProfile() : "" %>">
                                    <%= teacher.getProfile() != null && !teacher.getProfile().isEmpty() ? teacher.getProfile() : "-" %>
                                </div>
                            </td>
                            <td>
                                <% 
                                    int courseCount = teacher.getCourseIds() != null ? teacher.getCourseIds().size() : 0;
                                    if (courseCount > 0) {
                                %>
                                    <span class="course-count"><%= courseCount %> 门课程</span>
                                <% } else { %>
                                    <span style="color: #999;">无课程</span>
                                <% } %>
                            </td>
                            <td>
                                <div class="actions-cell">
                                    <a href="${pageContext.request.contextPath}/admin/teachers?action=edit&id=<%= teacher.getId() %>" 
                                       class="btn btn-warning">编辑</a>
                                    <button onclick="confirmDelete(<%= teacher.getId() %>, '<%= teacher.getUsername() %>')" 
                                            class="btn btn-danger">删除</button>
                                </div>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } else { %>
            <div class="no-data">
                <p>暂无教师数据</p>
                <p style="margin-top: 10px;">
                    <a href="${pageContext.request.contextPath}/admin/teachers?action=add" class="btn btn-primary">立即添加</a>
                </p>
            </div>
        <% } %>
    </div>
</body>
</html>
