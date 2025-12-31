<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.CourseModel" %>
<%@ page import="com.ulp.bean.UserModel" %>
<%@ page import="com.ulp.dao.UserDao" %>
<%@ page import="com.ulp.dao.impl.UserDaoImpl" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ulp.service.UserService" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= request.getAttribute("course") != null ? "编辑课程" : "添加课程" %> - 大学生学习平台</title>
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
        input[type="number"],
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
        input[type="number"]:focus,
        textarea:focus,
        select:focus {
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

        .show-teachers-btn {
            background-color: #17a2b8;
            color: white;
            border: none;
            padding: 8px 12px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            margin-top: 5px;
        }

        .show-teachers-btn:hover {
            background-color: #138496;
        }

        .teacher-list {
            max-height: 200px;
            overflow-y: auto;
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 10px;
            margin-top: 10px;
            display: none; /* 默认隐藏 */
        }

        .teacher-item {
            padding: 5px 0;
            border-bottom: 1px solid #eee;
        }

        .teacher-item:last-child {
            border-bottom: none;
        }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <%
        CourseModel course = (CourseModel) request.getAttribute("course");
        boolean isEdit = course != null;
    %>

    <h1><%= isEdit ? "编辑课程" : "添加新课程" %></h1>

    <%-- 显示错误消息 --%>
    <% if (request.getAttribute("error") != null) { %>
    <div class="message error"><%= request.getAttribute("error") %></div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/admin/courses">
        <% if (isEdit) { %>
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="id" value="<%= course.getId() %>">
        <% } else { %>
        <input type="hidden" name="action" value="add">
        <% } %>

        <div class="form-group">
            <label for="name">课程名称 <span class="required">*</span></label>
            <input type="text"
                   id="name"
                   name="name"
                   value="<%= isEdit ? course.getName() : "" %>"
                   required
                   placeholder="请输入课程名称">
        </div>

        <div class="form-group">
            <label for="teacherId">授课教师ID</label>
            <input type="number"
                   id="teacherId"
                   name="teacherId"
                   value="<%= isEdit && course.getTeacherId() != null ? course.getTeacherId() : "" %>"
                   placeholder="请输入教师ID，留空表示未分配">

            <!-- 显示教师列表按钮 -->
            <button type="button" class="show-teachers-btn" onclick="toggleTeacherList()">查看可用教师列表</button>

            <!-- 教师列表（默认隐藏） -->
            <div class="teacher-list" id="teacherList">
                <strong>可用教师列表：</strong>
                <%
                    UserDao userDao = new UserDaoImpl();
                    java.util.List<UserModel> teachers = userDao.getAllTeachers();
                    if (teachers != null && !teachers.isEmpty()) {
                        for (UserModel teacher : teachers) {
                %>
                <div class="teacher-item">
                    ID: <%= teacher.getId() %> - <%= teacher.getUsername() %> (<%= teacher.getEmail() %>)
                </div>
                <%
                    }
                } else {
                %>
                <div class="teacher-item">暂无教师用户</div>
                <%
                    }
                %>
            </div>
        </div>

        <div class="form-group">
            <label for="college">开课学院</label>
            <input type="text"
                   id="college"
                   name="college"
                   value="<%= isEdit && course.getCollege() != null ? course.getCollege() : "" %>"
                   placeholder="请输入开课学院名称">
        </div>

        <div class="form-group">
            <label for="visibility">课程可见性 <span class="required">*</span></label>
            <select id="visibility" name="visibility" required>
                <option value="all" <%= isEdit && "all".equals(course.getVisibility()) ? "selected" : (!isEdit ? "selected" : "") %>>
                    全部学生可见
                </option>
                <option value="class_only" <%= isEdit && "class_only".equals(course.getVisibility()) ? "selected" : "" %>>
                    仅本班学生可见
                </option>
            </select>
            <div class="help-text">选择课程的可见范围</div>
        </div>

        <div class="form-group">
            <label for="description">课程描述</label>
            <textarea id="description"
                      name="description"
                      placeholder="请输入课程描述信息"><%= isEdit && course.getDescription() != null ? course.getDescription() : "" %></textarea>
            <div class="help-text">课程的详细说明，包括课程目标、内容概要等</div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">
                <%= isEdit ? "保存修改" : "添加课程" %>
            </button>
            <a href="${pageContext.request.contextPath}/admin/courses" class="btn btn-secondary">取消</a>
        </div>
    </form>
</div>

<script>
    // 切换教师列表显示/隐藏
    function toggleTeacherList() {
        const teacherList = document.getElementById('teacherList');
        if (teacherList.style.display === 'block') {
            teacherList.style.display = 'none';
        } else {
            teacherList.style.display = 'block';
        }
    }

    // 在表单提交时验证教师ID
    document.querySelector('form').addEventListener('submit', function(e) {
        const teacherIdInput = document.getElementById('teacherId');
        if (teacherIdInput.value.trim() !== '') {
            const teacherId = parseInt(teacherIdInput.value);
            if (isNaN(teacherId)) {
                alert('教师ID必须是数字');
                e.preventDefault();
                return;
            }
        }
    });
</script>
</body>
</html>
